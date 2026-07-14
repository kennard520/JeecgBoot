package org.jeecg.modules.custom.api.mq;

import org.springframework.amqp.core.Message;

public interface CustomResultRetryPublisher {
    void publish(Message source, int retryAttempt);
}
