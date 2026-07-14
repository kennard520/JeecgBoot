package org.jeecg.modules.custom.api.callback;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class CustomCallbackPublisher {
    private static final List<Duration> RETRY_DELAYS = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofHours(2), Duration.ofHours(6));
    private static final Duration MAX_RETRY_AFTER = Duration.ofHours(6);
    private static final int MAX_ERROR_LENGTH = 1000;

    private final ICustomCallbackDeliveryService deliveryService;
    private final CallbackUrlPolicy urlPolicy;
    private final CallbackSecretCipher secretCipher;
    private final CallbackHttpTransport transport;
    private final Clock clock;
    private final int batchSize;
    private final long claimTimeoutSeconds;
    private final String publisherId;

    @Autowired
    public CustomCallbackPublisher(
            ICustomCallbackDeliveryService deliveryService,
            CallbackUrlPolicy urlPolicy,
            CallbackSecretCipher secretCipher,
            CallbackHttpTransport transport,
            @Value("${custom.api.callback.batch-size:20}") int batchSize,
            @Value("${custom.api.callback.claim-timeout-seconds:300}") long claimTimeoutSeconds,
            @Value("${custom.api.callback.publisher-id:${HOSTNAME:java}}") String publisherId) {
        this(deliveryService, urlPolicy, secretCipher, transport, Clock.systemUTC(),
                batchSize, claimTimeoutSeconds, normalizedPublisherId(publisherId));
    }

    public CustomCallbackPublisher(ICustomCallbackDeliveryService deliveryService,
                                   CallbackUrlPolicy urlPolicy,
                                   CallbackSecretCipher secretCipher,
                                   CallbackHttpTransport transport,
                                   Clock clock,
                                   int batchSize,
                                   long claimTimeoutSeconds,
                                   String publisherId) {
        this.deliveryService = deliveryService;
        this.urlPolicy = urlPolicy;
        this.secretCipher = secretCipher;
        this.transport = transport;
        this.clock = clock;
        this.batchSize = batchSize;
        this.claimTimeoutSeconds = claimTimeoutSeconds;
        this.publisherId = publisherId;
    }

    @Scheduled(fixedDelayString = "${custom.api.callback.publish-interval-ms:1000}",
            scheduler = "customCallbackTaskScheduler")
    public void publishPending() {
        deliveryService.releaseStaleClaims(claimTimeoutSeconds);
        for (CustomCallbackDelivery delivery : deliveryService.findDue(batchSize)) {
            String claimToken = deliveryService.claim(delivery.getId(), publisherId);
            if (claimToken == null) {
                continue;
            }
            delivery.setClaimToken(claimToken).setClaimedBy(publisherId);
            publishOne(delivery, claimToken);
        }
    }

    private void publishOne(CustomCallbackDelivery delivery, String claimToken) {
        CallbackHttpResponse response;
        try {
            ValidatedCallbackTarget target = urlPolicy.resolveAndValidate(delivery.getCallbackUrl());
            String secret = secretCipher.decrypt(
                    delivery.getSecretCiphertext(), delivery.getSecretKeyVersion());
            byte[] body = delivery.getPayloadJson().getBytes(StandardCharsets.UTF_8);
            String timestamp = String.valueOf(clock.instant().getEpochSecond());
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", "application/json; charset=utf-8");
            headers.put("X-CustomsAI-Delivery-Id", delivery.getDeliveryId());
            headers.put("X-CustomsAI-Task-Id", delivery.getTaskId());
            headers.put("X-CustomsAI-Timestamp", timestamp);
            headers.put("X-CustomsAI-Signature", "v1=" + signature(secret, timestamp, body));

            response = transport.send(target, body, headers);
        } catch (CallbackPolicyViolationException | IllegalArgumentException permanent) {
            deliveryService.markPermanentFailure(
                    delivery, claimToken, null, message(permanent));
            return;
        } catch (CallbackDnsException | CallbackConfigurationException retryable) {
            retryOrDead(delivery, claimToken, null, message(retryable),
                    retryDelay(delivery, null));
            return;
        } catch (Exception transportOrConfig) {
            retryOrDead(delivery, claimToken, null, message(transportOrConfig),
                    retryDelay(delivery, null));
            return;
        }

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            deliveryService.markSucceeded(delivery, claimToken, response.statusCode());
            return;
        }
        String error = responseError(response);
        if (isRetryable(response.statusCode())) {
            retryOrDead(delivery, claimToken, response.statusCode(), error,
                    retryDelay(delivery, response));
        } else {
            deliveryService.markPermanentFailure(
                    delivery, claimToken, response.statusCode(), error);
        }
    }

    private void retryOrDead(CustomCallbackDelivery delivery, String claimToken, Integer httpStatus,
                             String error, Duration delay) {
        int attempts = delivery.getAttemptCount() == null ? 0 : delivery.getAttemptCount();
        if (attempts >= RETRY_DELAYS.size()) {
            deliveryService.markPermanentFailure(delivery, claimToken, httpStatus, error);
            return;
        }
        deliveryService.scheduleRetry(delivery, claimToken, httpStatus, error, delay);
    }

    private Duration retryDelay(CustomCallbackDelivery delivery, CallbackHttpResponse response) {
        Duration retryAfter = parseRetryAfter(response);
        if (retryAfter != null) {
            return retryAfter.compareTo(MAX_RETRY_AFTER) > 0 ? MAX_RETRY_AFTER : retryAfter;
        }
        int attempts = delivery.getAttemptCount() == null ? 0 : delivery.getAttemptCount();
        return RETRY_DELAYS.get(Math.min(attempts, RETRY_DELAYS.size() - 1));
    }

    private Duration parseRetryAfter(CallbackHttpResponse response) {
        if (response == null || response.headers() == null) {
            return null;
        }
        String value = response.headers().entrySet().stream()
                .filter(entry -> "retry-after".equals(entry.getKey().toLowerCase(Locale.ROOT)))
                .flatMap(entry -> entry.getValue().stream())
                .findFirst().orElse(null);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            long seconds = Long.parseLong(value.trim());
            return Duration.ofSeconds(Math.max(1L, seconds));
        } catch (NumberFormatException ignored) {
            try {
                Instant retryAt = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                Duration delay = Duration.between(clock.instant(), retryAt);
                return delay.isNegative() || delay.isZero() ? Duration.ofSeconds(1) : delay;
            } catch (Exception invalidDate) {
                return null;
            }
        }
    }

    private boolean isRetryable(int status) {
        return status == 408 || status == 425 || status == 429 || status >= 500;
    }

    private String signature(String secret, String timestamp, byte[] body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            mac.update((timestamp + ".").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(mac.doFinal(body));
        } catch (Exception e) {
            throw new IllegalStateException("sign callback payload failed", e);
        }
    }

    private String responseError(CallbackHttpResponse response) {
        return truncate("HTTP " + response.statusCode() + ": "
                + (response.body() == null ? "" : response.body()));
    }

    private String message(Exception error) {
        return truncate(error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage());
    }

    private String truncate(String value) {
        return value == null || value.length() <= MAX_ERROR_LENGTH
                ? value : value.substring(0, MAX_ERROR_LENGTH);
    }

    private static String normalizedPublisherId(String value) {
        String prefix = value == null || value.isBlank() ? "java" : value.trim();
        return prefix + "-" + UUID.randomUUID();
    }
}
