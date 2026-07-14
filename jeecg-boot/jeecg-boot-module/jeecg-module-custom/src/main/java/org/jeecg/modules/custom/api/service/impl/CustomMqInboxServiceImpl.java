package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.api.entity.CustomMqInbox;
import org.jeecg.modules.custom.api.mapper.CustomMqInboxMapper;
import org.jeecg.modules.custom.api.service.ICustomMqInboxService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomMqInboxServiceImpl
        extends ServiceImpl<CustomMqInboxMapper, CustomMqInbox>
        implements ICustomMqInboxService {
    private static final int MAX_ERROR_LENGTH = 1000;
    private final CustomMqInboxMapper inboxMapper;

    public CustomMqInboxServiceImpl(CustomMqInboxMapper inboxMapper) {
        this.inboxMapper = inboxMapper;
    }

    @Override
    public CustomMqInbox receive(String eventId, String taskId, Integer runNo,
                                 String eventType, String payloadHash) {
        if (findByEventId(eventId) != null) {
            return null;
        }
        CustomMqInbox inbox = new CustomMqInbox()
                .setEventId(eventId)
                .setTaskId(taskId)
                .setRunNo(runNo)
                .setEventType(eventType)
                .setPayloadHash(payloadHash)
                .setStatus(CustomMqInbox.STATUS_RECEIVED)
                .setReceivedAt(LocalDateTime.now());
        try {
            return inboxMapper.insert(inbox) == 1 ? inbox : null;
        } catch (DuplicateKeyException duplicate) {
            return null;
        }
    }

    @Override
    public void markProcessed(CustomMqInbox inbox) {
        updateStatus(inbox, CustomMqInbox.STATUS_PROCESSED, null);
    }

    @Override
    public void markIgnored(CustomMqInbox inbox, String reason) {
        updateStatus(inbox, CustomMqInbox.STATUS_IGNORED, truncate(reason));
    }

    private CustomMqInbox findByEventId(String eventId) {
        return inboxMapper.selectOne(new LambdaQueryWrapper<CustomMqInbox>()
                .eq(CustomMqInbox::getEventId, eventId));
    }

    private void updateStatus(CustomMqInbox inbox, String status, String error) {
        if (inbox == null || inbox.getId() == null) {
            return;
        }
        inbox.setStatus(status).setErrorMessage(error).setProcessedAt(LocalDateTime.now());
        inboxMapper.updateById(inbox);
    }

    private String truncate(String value) {
        return value == null || value.length() <= MAX_ERROR_LENGTH
                ? value : value.substring(0, MAX_ERROR_LENGTH);
    }
}
