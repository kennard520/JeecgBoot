package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.callback.CallbackSecretCipher;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mapper.CustomCallbackDeliveryMapper;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomCallbackDeliveryServiceImpl
        extends ServiceImpl<CustomCallbackDeliveryMapper, CustomCallbackDelivery>
        implements ICustomCallbackDeliveryService {
    private static final int MAX_ERROR_LENGTH = 1000;

    private final CustomCallbackDeliveryMapper deliveryMapper;
    private final CustomApiTaskMapper taskMapper;
    private final CallbackSecretCipher secretCipher;
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    public CustomCallbackDeliveryServiceImpl(CustomCallbackDeliveryMapper deliveryMapper,
                                             CustomApiTaskMapper taskMapper,
                                             CallbackSecretCipher secretCipher) {
        this.deliveryMapper = deliveryMapper;
        this.taskMapper = taskMapper;
        this.secretCipher = secretCipher;
    }

    @Override
    public CustomCallbackDelivery enqueueTerminal(CustomApiTask task, String eventType, Object result,
                                                   String errorCode, String errorMessage) {
        if (task == null || isBlank(task.getCallbackUrl()) || "polling".equals(task.getResponseMode())) {
            return null;
        }
        CustomCallbackDelivery existing = findTerminal(task, eventType);
        if (existing != null) {
            return existing;
        }

        String ciphertext = task.getCallbackSecretCiphertext();
        String keyVersion = task.getCallbackSecretKeyVersion();
        if (isBlank(ciphertext) || isBlank(keyVersion)) {
            if (isBlank(task.getCallbackSecret())) {
                throw new JeecgBootException("callback secret is unavailable");
            }
            CallbackSecretCipher.EncryptedSecret encrypted = secretCipher.encrypt(task.getCallbackSecret());
            ciphertext = encrypted.ciphertext();
            keyVersion = encrypted.keyVersion();
            task.setCallbackSecretCiphertext(ciphertext)
                    .setCallbackSecretKeyVersion(keyVersion)
                    .setCallbackSecret(null);
            taskMapper.updateById(task);
        }

        LocalDateTime now = LocalDateTime.now();
        String deliveryId = "delivery-" + UUID.randomUUID();
        String payload = payload(deliveryId, task, eventType, result, errorCode, errorMessage);
        CustomCallbackDelivery delivery = new CustomCallbackDelivery()
                .setDeliveryId(deliveryId)
                .setTaskId(task.getTaskId())
                .setRunNo(task.getCustomsAiRunNo())
                .setCustomerCode(task.getCustomerCode())
                .setEventType(eventType)
                .setCallbackUrl(task.getCallbackUrl())
                .setSecretCiphertext(ciphertext)
                .setSecretKeyVersion(keyVersion)
                .setPayloadJson(payload)
                .setPayloadHash(sha256(payload))
                .setStatus(CustomCallbackDelivery.STATUS_PENDING)
                .setAttemptCount(0)
                .setNextAttemptAt(now)
                .setCreatedAt(now)
                .setUpdatedAt(now);
        try {
            deliveryMapper.insert(delivery);
            return delivery;
        } catch (DuplicateKeyException duplicate) {
            CustomCallbackDelivery raced = findTerminal(task, eventType);
            if (raced != null) {
                return raced;
            }
            throw duplicate;
        }
    }

    @Override
    public List<CustomCallbackDelivery> findDue(int limit) {
        int pageSize = Math.max(1, Math.min(limit, 200));
        LocalDateTime now = LocalDateTime.now();
        return deliveryMapper.selectPage(new Page<>(1, pageSize),
                new LambdaQueryWrapper<CustomCallbackDelivery>()
                        .eq(CustomCallbackDelivery::getStatus, CustomCallbackDelivery.STATUS_PENDING)
                        .and(wrapper -> wrapper.isNull(CustomCallbackDelivery::getNextAttemptAt)
                                .or().le(CustomCallbackDelivery::getNextAttemptAt, now))
                        .orderByAsc(CustomCallbackDelivery::getId)).getRecords();
    }

    @Override
    public boolean claim(Long id) {
        return id != null && deliveryMapper.claim(id, LocalDateTime.now()) == 1;
    }

    @Override
    public void markSucceeded(CustomCallbackDelivery delivery, int httpStatus) {
        LocalDateTime now = LocalDateTime.now();
        if (deliveryMapper.markSucceeded(delivery.getId(), httpStatus, now) != 1) {
            throw new JeecgBootException("callback delivery is not in sending state");
        }
        updateTaskCallback(delivery.getTaskId(), "success", null);
    }

    @Override
    public void scheduleRetry(CustomCallbackDelivery delivery, Integer httpStatus,
                              String error, Duration delay) {
        int attempts = attemptCount(delivery) + 1;
        LocalDateTime now = LocalDateTime.now();
        deliveryMapper.scheduleRetry(delivery.getId(), attempts,
                now.plus(delay == null ? Duration.ofMinutes(1) : delay), httpStatus,
                truncate(error), now);
        updateTaskCallback(delivery.getTaskId(), "retrying", truncate(error));
    }

    @Override
    public void markPermanentFailure(CustomCallbackDelivery delivery, Integer httpStatus, String error) {
        LocalDateTime now = LocalDateTime.now();
        deliveryMapper.markPermanentFailure(delivery.getId(), attemptCount(delivery) + 1,
                httpStatus, truncate(error), now);
        updateTaskCallback(delivery.getTaskId(), "failed", truncate(error));
    }

    @Override
    public void releaseStaleClaims(long claimTimeoutSeconds) {
        LocalDateTime now = LocalDateTime.now();
        deliveryMapper.releaseStaleClaims(now.minusSeconds(Math.max(1L, claimTimeoutSeconds)), now);
    }

    private CustomCallbackDelivery findTerminal(CustomApiTask task, String eventType) {
        return deliveryMapper.selectOne(new LambdaQueryWrapper<CustomCallbackDelivery>()
                .eq(CustomCallbackDelivery::getTaskId, task.getTaskId())
                .eq(CustomCallbackDelivery::getRunNo, task.getCustomsAiRunNo())
                .eq(CustomCallbackDelivery::getEventType, eventType));
    }

    private String payload(String deliveryId, CustomApiTask task, String eventType, Object result,
                           String errorCode, String errorMessage) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("deliveryId", deliveryId);
        body.put("event", eventType);
        body.put("taskId", task.getTaskId());
        body.put("clientTaskId", task.getClientTaskId());
        body.put("fileId", task.getFileId());
        body.put("runNo", task.getCustomsAiRunNo());
        body.put("status", task.getStatus());
        body.put("declareData", result);
        if (!isBlank(errorCode) || !isBlank(errorMessage)) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("code", errorCode);
            error.put("message", errorMessage);
            body.put("error", error);
        } else {
            body.put("error", null);
        }
        body.put("finishedAt", task.getFinishedAt() == null ? null : task.getFinishedAt().toString());
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new JeecgBootException("serialize callback payload failed: " + e.getMessage());
        }
    }

    private String sha256(String payload) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private void updateTaskCallback(String taskId, String status, String error) {
        taskMapper.update(null, new LambdaUpdateWrapper<CustomApiTask>()
                .eq(CustomApiTask::getTaskId, taskId)
                .set(CustomApiTask::getCallbackStatus, status)
                .set(CustomApiTask::getCallbackError, error));
    }

    private int attemptCount(CustomCallbackDelivery delivery) {
        return delivery.getAttemptCount() == null ? 0 : delivery.getAttemptCount();
    }

    private String truncate(String value) {
        return value == null || value.length() <= MAX_ERROR_LENGTH
                ? value : value.substring(0, MAX_ERROR_LENGTH);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
