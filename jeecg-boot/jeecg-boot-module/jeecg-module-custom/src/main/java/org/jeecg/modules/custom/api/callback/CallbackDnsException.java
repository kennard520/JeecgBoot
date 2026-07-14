package org.jeecg.modules.custom.api.callback;

public class CallbackDnsException extends RuntimeException {
    public CallbackDnsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallbackDnsException(String message) {
        super(message);
    }
}
