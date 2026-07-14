package org.jeecg.modules.custom.api.mq;

public class OutboxClaimLostException extends RuntimeException {
    public OutboxClaimLostException(String message) {
        super(message);
    }
}
