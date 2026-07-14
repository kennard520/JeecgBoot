package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.mapper.CustomMqOutboxMapper;
import org.jeecg.modules.custom.api.mq.CustomApiMqConstant;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomMqOutboxServiceImpl
        extends ServiceImpl<CustomMqOutboxMapper, CustomMqOutbox>
        implements ICustomMqOutboxService {
    private static final String AGGREGATE_TYPE_TASK = "TASK";
    private static final String EVENT_TYPE_PARSE_REQUESTED = "parse.requested";
    private static final int MAX_ERROR_LENGTH = 1000;

    private final CustomMqOutboxMapper outboxMapper;
    private final String internalBaseUrl;
    private final String internalToken;
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Autowired
    public CustomMqOutboxServiceImpl(
            CustomMqOutboxMapper outboxMapper,
            @Value("${custom.api.internal-base-url:http://localhost:8080}") String internalBaseUrl,
            @Value("${custom.api.internal-token:}") String internalToken) {
        this.outboxMapper = outboxMapper;
        this.internalBaseUrl = trimTrailingSlash(internalBaseUrl);
        this.internalToken = internalToken == null ? "" : internalToken;
    }

    @Override
    public CustomMqOutbox enqueueParseTask(CustomApiTask task, CustomApiFile file, int runNo) {
        validateRequest(task, file, runNo);
        CustomMqOutbox existing = findAggregate(task.getTaskId(), runNo);
        if (existing != null) {
            return existing;
        }

        LocalDateTime now = LocalDateTime.now();
        String eventId = "request-" + UUID.randomUUID();
        CustomMqOutbox event = new CustomMqOutbox()
                .setEventId(eventId)
                .setAggregateType(AGGREGATE_TYPE_TASK)
                .setAggregateId(task.getTaskId())
                .setAggregateVersion(runNo)
                .setEventType(EVENT_TYPE_PARSE_REQUESTED)
                .setExchangeName(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE)
                .setRoutingKey(task.getCompanyCode().trim())
                .setPayloadJson(buildPayload(eventId, task, file, runNo))
                .setStatus(CustomMqOutbox.STATUS_PENDING)
                .setAttemptCount(0)
                .setNextAttemptAt(now)
                .setCreatedAt(now)
                .setUpdatedAt(now);
        try {
            outboxMapper.insert(event);
            return event;
        } catch (DuplicateKeyException duplicate) {
            CustomMqOutbox raced = findAggregate(task.getTaskId(), runNo);
            if (raced != null) {
                return raced;
            }
            throw duplicate;
        }
    }

    @Override
    public List<CustomMqOutbox> findPublishable(int limit) {
        int pageSize = Math.max(1, Math.min(limit, 200));
        LocalDateTime now = LocalDateTime.now();
        return outboxMapper.selectPage(new Page<>(1, pageSize),
                new LambdaQueryWrapper<CustomMqOutbox>()
                        .eq(CustomMqOutbox::getStatus, CustomMqOutbox.STATUS_PENDING)
                        .and(wrapper -> wrapper.isNull(CustomMqOutbox::getNextAttemptAt)
                                .or().le(CustomMqOutbox::getNextAttemptAt, now))
                        .orderByAsc(CustomMqOutbox::getId)).getRecords();
    }

    @Override
    public boolean claim(Long id) {
        return id != null && outboxMapper.claim(id, LocalDateTime.now()) == 1;
    }

    @Override
    public void markSent(Long id) {
        if (id == null || outboxMapper.markSent(id, LocalDateTime.now()) != 1) {
            throw new JeecgBootException("outbox event is not in sending state");
        }
    }

    @Override
    public void reschedule(CustomMqOutbox event, String error, int maxAttempts,
                           long baseDelaySeconds, long maxDelaySeconds) {
        if (event == null || event.getId() == null) {
            return;
        }
        int attempts = (event.getAttemptCount() == null ? 0 : event.getAttemptCount()) + 1;
        boolean exhausted = attempts >= Math.max(1, maxAttempts);
        long exponent = Math.min(30, Math.max(0, attempts - 1));
        long delay = Math.min(Math.max(1L, maxDelaySeconds),
                Math.max(1L, baseDelaySeconds) * (1L << exponent));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = exhausted ? null : now.plusSeconds(delay);
        String status = exhausted ? CustomMqOutbox.STATUS_DEAD : CustomMqOutbox.STATUS_PENDING;
        outboxMapper.reschedule(event.getId(), status, attempts, next,
                truncate(error, MAX_ERROR_LENGTH), now);
        event.setStatus(status).setAttemptCount(attempts).setNextAttemptAt(next)
                .setClaimedAt(null).setLastError(truncate(error, MAX_ERROR_LENGTH)).setUpdatedAt(now);
    }

    @Override
    public void releaseStaleClaims(long claimTimeoutSeconds) {
        LocalDateTime now = LocalDateTime.now();
        outboxMapper.releaseStaleClaims(now.minusSeconds(Math.max(1L, claimTimeoutSeconds)), now);
    }

    private CustomMqOutbox findAggregate(String taskId, int runNo) {
        return outboxMapper.selectOne(new LambdaQueryWrapper<CustomMqOutbox>()
                .eq(CustomMqOutbox::getAggregateType, AGGREGATE_TYPE_TASK)
                .eq(CustomMqOutbox::getAggregateId, taskId)
                .eq(CustomMqOutbox::getAggregateVersion, runNo)
                .eq(CustomMqOutbox::getEventType, EVENT_TYPE_PARSE_REQUESTED));
    }

    private String buildPayload(String eventId, CustomApiTask task, CustomApiFile file, int runNo) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("eventId", eventId);
        body.put("eventType", EVENT_TYPE_PARSE_REQUESTED);
        body.put("schemaVersion", 2);
        body.put("taskId", task.getTaskId());
        body.put("runNo", runNo);
        body.put("attemptNo", 1);
        body.put("maxAttempts", 3);
        body.put("customerCode", task.getCustomerCode());
        body.put("agentCode", task.getCompanyCode());
        body.put("companyCode", task.getCompanyCode());
        body.put("fileId", file.getFileId());
        body.put("clientTaskId", task.getClientTaskId());
        body.put("direction", task.getDirection());
        body.put("originalFilename", file.getOriginalFilename());
        body.put("contentType", file.getContentType());
        body.put("fileSize", file.getActualFileSize());
        body.put("sha256", file.getActualSha256().trim().toLowerCase(Locale.ROOT));
        body.put("downloadUrl", internalBaseUrl + "/custom/api/internal/tasks/"
                + task.getTaskId() + "/files/" + file.getFileId() + "/download");
        if (!internalToken.isBlank()) {
            body.put("downloadHeaders", Map.of("X-Custom-Api-Internal-Token", internalToken));
        }
        if (task.getMetadataJson() != null && !task.getMetadataJson().isBlank()) {
            try {
                body.put("metadata", objectMapper.readTree(task.getMetadataJson()));
            } catch (Exception invalidJson) {
                throw new JeecgBootException("task metadata is not valid JSON");
            }
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new JeecgBootException("serialize parse request failed: " + e.getMessage());
        }
    }

    private void validateRequest(CustomApiTask task, CustomApiFile file, int runNo) {
        if (task == null || isBlank(task.getTaskId()) || runNo < 1) {
            throw new JeecgBootException("taskId and positive runNo are required");
        }
        if (isBlank(task.getCustomerCode()) || isBlank(task.getCompanyCode())) {
            throw new JeecgBootException("customerCode and agentCode are required");
        }
        if (file == null || isBlank(file.getFileId())
                || !CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            throw new JeecgBootException("verified uploaded file is required");
        }
        if (file.getActualFileSize() == null || file.getActualFileSize() < 1) {
            throw new JeecgBootException("positive actual file size is required for schema v2");
        }
        String hash = file.getActualSha256();
        if (hash == null || !hash.trim().matches("(?i)[0-9a-f]{64}")) {
            throw new JeecgBootException("64-hex actual sha256 is required for schema v2");
        }
        if (!task.getFileId().equals(file.getFileId())) {
            throw new JeecgBootException("task file does not match uploaded file");
        }
        if (!task.getCustomerCode().equals(file.getCustomerCode())) {
            throw new JeecgBootException("task customer does not match uploaded file");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private static String trimTrailingSlash(String value) {
        String result = value == null || value.isBlank() ? "http://localhost:8080" : value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
