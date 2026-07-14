package org.jeecg.modules.custom.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class InternalDownloadTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String internalBaseUrl;
    private final byte[] secret;
    private final long ttlSeconds;
    private final Clock clock;

    @Autowired
    public InternalDownloadTokenService(
            @Value("${custom.api.internal-base-url}") String internalBaseUrl,
            @Value("${custom.api.internal-download-secret}") String secret,
            @Value("${custom.api.internal-download-ttl-seconds:300}") long ttlSeconds) {
        this(internalBaseUrl, secret, ttlSeconds, Clock.systemUTC());
    }

    public InternalDownloadTokenService(String internalBaseUrl, String secret,
                                        long ttlSeconds, Clock clock) {
        if (internalBaseUrl == null || internalBaseUrl.isBlank()) {
            throw new IllegalStateException("custom.api.internal-base-url is required");
        }
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "custom.api.internal-download-secret must contain at least 32 characters");
        }
        if (ttlSeconds < 1) {
            throw new IllegalStateException("custom.api.internal-download-ttl-seconds must be positive");
        }
        this.internalBaseUrl = trimTrailingSlash(internalBaseUrl);
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttlSeconds = ttlSeconds;
        this.clock = clock;
    }

    public DownloadGrant issue(String taskId, String fileId, int runNo) {
        requireIdentifiers(taskId, fileId, runNo);
        long expiresAt = Instant.now(clock).getEpochSecond() + ttlSeconds;
        String signature = sign(taskId, fileId, runNo, expiresAt);
        String url = internalBaseUrl + "/custom/api/internal/tasks/"
                + encode(taskId) + "/files/" + encode(fileId) + "/download"
                + "?runNo=" + runNo + "&expires=" + expiresAt
                + "&signature=" + signature;
        return new DownloadGrant(url, expiresAt, signature);
    }

    public void validate(String taskId, String fileId, int runNo,
                         long expiresAt, String signature) {
        if (taskId == null || taskId.isBlank() || fileId == null || fileId.isBlank()
                || runNo < 1 || expiresAt < 1 || signature == null || signature.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "missing internal download authorization");
        }
        if (expiresAt < Instant.now(clock).getEpochSecond()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "internal download authorization expired");
        }
        byte[] expected = sign(taskId, fileId, runNo, expiresAt)
                .getBytes(StandardCharsets.US_ASCII);
        byte[] actual = signature.toLowerCase().getBytes(StandardCharsets.US_ASCII);
        if (!MessageDigest.isEqual(expected, actual)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "invalid internal download authorization");
        }
    }

    private String sign(String taskId, String fileId, int runNo, long expiresAt) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            String payload = taskId + "\n" + fileId + "\n" + runNo + "\n" + expiresAt;
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("cannot sign internal download authorization", e);
        }
    }

    private void requireIdentifiers(String taskId, String fileId, int runNo) {
        if (taskId == null || taskId.isBlank() || fileId == null || fileId.isBlank() || runNo < 1) {
            throw new IllegalArgumentException("taskId, fileId and positive runNo are required");
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String trimTrailingSlash(String value) {
        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public record DownloadGrant(String url, long expiresAt, String signature) {
    }
}
