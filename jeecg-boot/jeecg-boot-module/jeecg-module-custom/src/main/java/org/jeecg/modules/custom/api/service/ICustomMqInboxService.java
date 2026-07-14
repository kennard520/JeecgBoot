package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.custom.api.entity.CustomMqInbox;

public interface ICustomMqInboxService extends IService<CustomMqInbox> {
    CustomMqInbox receive(String eventId, String taskId, Integer runNo,
                          String eventType, String payloadHash);

    void markProcessed(CustomMqInbox inbox);

    void markIgnored(CustomMqInbox inbox, String reason);
}
