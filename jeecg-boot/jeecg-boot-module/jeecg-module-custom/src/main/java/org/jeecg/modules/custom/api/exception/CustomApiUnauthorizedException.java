package org.jeecg.modules.custom.api.exception;

public class CustomApiUnauthorizedException extends RuntimeException {
    public CustomApiUnauthorizedException(String message) {
        super(message);
    }
}
