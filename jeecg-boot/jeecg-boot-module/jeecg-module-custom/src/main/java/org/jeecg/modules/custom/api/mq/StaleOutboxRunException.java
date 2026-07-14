package org.jeecg.modules.custom.api.mq;

public class StaleOutboxRunException extends RuntimeException {
    public StaleOutboxRunException(String message) {
        super(message);
    }
}
