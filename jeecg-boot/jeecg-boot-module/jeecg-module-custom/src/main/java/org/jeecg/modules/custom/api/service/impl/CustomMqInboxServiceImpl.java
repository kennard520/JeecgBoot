package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.entity.CustomMqInbox;
import org.jeecg.modules.custom.api.mapper.CustomMqInboxMapper;
import org.jeecg.modules.custom.api.service.ICustomMqInboxService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
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
        CustomMqInbox existing = findByEventId(eventId);
        if (existing != null) {
            return resolveDuplicate(existing, taskId, runNo, payloadHash);
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
            CustomMqInbox winner = findByEventId(eventId);
            if (winner == null) {
                throw duplicate;
            }
            return resolveDuplicate(winner, taskId, runNo, payloadHash);
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

    private CustomMqInbox resolveDuplicate(CustomMqInbox existing, String taskId,
                                            Integer runNo, String payloadHash) {
        if (Objects.equals(existing.getTaskId(), taskId)
                && Objects.equals(existing.getRunNo(), runNo)
                && equalHash(existing.getPayloadHash(), payloadHash)) {
            return null;
        }
        String message = "inbox event " + existing.getEventId()
                + " conflicts with its committed task/run/payload identity";
        log.error("CUSTOM_MQ_INBOX_CONFLICT eventId={}, storedTaskId={}, incomingTaskId={}, "
                        + "storedRunNo={}, incomingRunNo={}",
                existing.getEventId(), existing.getTaskId(), taskId,
                existing.getRunNo(), runNo);
        throw new IllegalStateException(message);
    }

    private boolean equalHash(String left, String right) {
        return left == null ? right == null : right != null && left.equalsIgnoreCase(right);
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
