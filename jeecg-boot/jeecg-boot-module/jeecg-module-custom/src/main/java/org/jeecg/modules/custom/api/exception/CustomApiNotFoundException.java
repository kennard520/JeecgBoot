package org.jeecg.modules.custom.api.exception;

public class CustomApiNotFoundException extends RuntimeException {
    public CustomApiNotFoundException(String message) {
        super(message);
    }
}
