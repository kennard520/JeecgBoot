package org.jeecg.modules.custom.api.util;

import org.jeecg.common.exception.JeecgBootException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class CustomApiCrypto {
    private static final SecureRandom RANDOM = new SecureRandom();

    private CustomApiCrypto() {
    }

    public static String randomToken(String prefix, int bytes) {
        byte[] data = new byte[bytes];
        RANDOM.nextBytes(data);
        return prefix + HexFormat.of().formatHex(data);
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new JeecgBootException("SHA-256 failed");
        }
    }

    public static String hmacSha256(String secret, byte[] body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return "sha256=" + HexFormat.of().formatHex(mac.doFinal(body));
        } catch (Exception e) {
            throw new JeecgBootException("HMAC-SHA256 failed");
        }
    }

    public static boolean equalsHash(String raw, String hash) {
        if (raw == null || hash == null) {
            return false;
        }
        return MessageDigest.isEqual(sha256(raw).getBytes(StandardCharsets.UTF_8), hash.getBytes(StandardCharsets.UTF_8));
    }
}
