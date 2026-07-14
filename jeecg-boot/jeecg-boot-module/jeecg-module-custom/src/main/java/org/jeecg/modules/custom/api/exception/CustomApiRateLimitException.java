package org.jeecg.modules.custom.api.exception;

public class CustomApiRateLimitException extends RuntimeException {
    private final long retryAfterSeconds;

    public CustomApiRateLimitException(long retryAfterSeconds) {
        super("rate limit exceeded");
        this.retryAfterSeconds = Math.max(1L, retryAfterSeconds);
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
