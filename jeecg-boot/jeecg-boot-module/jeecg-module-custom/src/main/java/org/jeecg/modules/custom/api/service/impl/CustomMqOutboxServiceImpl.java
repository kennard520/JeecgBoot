package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mapper.CustomMqOutboxMapper;
import org.jeecg.modules.custom.api.mq.CustomApiMqConstant;
import org.jeecg.modules.custom.api.mq.OutboxClaimLostException;
import org.jeecg.modules.custom.api.mq.StaleOutboxRunException;
import org.jeecg.modules.custom.api.security.InternalDownloadTokenService;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CustomMqOutboxServiceImpl
        extends ServiceImpl<CustomMqOutboxMapper, CustomMqOutbox>
        implements ICustomMqOutboxService {
    private static final String AGGREGATE_TYPE_TASK = "TASK";
    private static final String EVENT_TYPE_PARSE_REQUESTED = "parse.requested";
    private static final int MAX_ERROR_LENGTH = 1000;

    private final CustomMqOutboxMapper outboxMapper;
    private final InternalDownloadTokenService downloadTokenService;
    private final CustomApiTaskMapper taskMapper;
    private final IDocumentService documentService;
    private final ICustomCallbackDeliveryService callbackDeliveryService;
    private final org.jeecg.modules.custom.api.service.ICustomApiFileService fileService;
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    public CustomMqOutboxServiceImpl(
            CustomMqOutboxMapper outboxMapper,
            InternalDownloadTokenService downloadTokenService) {
        this(outboxMapper, downloadTokenService, null, null, null, null);
    }

    @Autowired
    public CustomMqOutboxServiceImpl(
            CustomMqOutboxMapper outboxMapper,
            InternalDownloadTokenService downloadTokenService,
            CustomApiTaskMapper taskMapper,
            @Lazy IDocumentService documentService,
            ICustomCallbackDeliveryService callbackDeliveryService,
            org.jeecg.modules.custom.api.service.ICustomApiFileService fileService) {
        this.outboxMapper = outboxMapper;
        this.downloadTokenService = downloadTokenService;
        this.taskMapper = taskMapper;
        this.documentService = documentService;
        this.callbackDeliveryService = callbackDeliveryService;
        this.fileService = fileService;
    }

    public CustomMqOutboxServiceImpl(
            CustomMqOutboxMapper outboxMapper,
            InternalDownloadTokenService downloadTokenService,
            CustomApiTaskMapper taskMapper,
            IDocumentService documentService,
            ICustomCallbackDeliveryService callbackDeliveryService) {
        this(outboxMapper, downloadTokenService, taskMapper, documentService,
                callbackDeliveryService, null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
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
                .setPayloadJson(buildPayload(eventId, task, file, runNo, false))
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
    public String claim(Long id, String claimedBy) {
        if (id == null || claimedBy == null || claimedBy.isBlank()) {
            return null;
        }
        String claimToken = UUID.randomUUID().toString();
        return outboxMapper.claim(id, claimToken, claimedBy, LocalDateTime.now()) == 1
                ? claimToken : null;
    }

    @Override
    public CustomMqOutbox prepareForPublish(CustomMqOutbox event, String claimToken,
                                            CustomApiTask task, CustomApiFile file) {
        if (event == null || event.getId() == null || claimToken == null || claimToken.isBlank()
                || !CustomMqOutbox.STATUS_SENDING.equals(event.getStatus())) {
            throw new OutboxClaimLostException("outbox claim is required before payload refresh");
        }
        int currentRun = task == null || task.getCustomsAiRunNo() == null
                ? 1 : task.getCustomsAiRunNo();
        if (task == null || !event.getAggregateId().equals(task.getTaskId())
                || event.getAggregateVersion() == null
                || event.getAggregateVersion() != currentRun) {
            throw new StaleOutboxRunException("outbox aggregate run is no longer current");
        }
        validateRequest(task, file, currentRun);
        String payload = buildPayload(event.getEventId(), task, file, currentRun, true);
        LocalDateTime now = LocalDateTime.now();
        if (outboxMapper.refreshPayload(event.getId(), claimToken, payload, now) != 1) {
            throw new OutboxClaimLostException("outbox claim was lost before payload refresh");
        }
        event.setPayloadJson(payload).setUpdatedAt(now);
        return event;
    }

    @Override
    public void markSent(CustomMqOutbox event, String claimToken) {
        if (event == null || event.getId() == null || claimToken == null
                || outboxMapper.markSent(event.getId(), claimToken,
                event.getAggregateId(), event.getAggregateVersion(), LocalDateTime.now()) != 1) {
            throw new OutboxClaimLostException("outbox publisher claim was lost before confirm");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reschedule(CustomMqOutbox event, String claimToken, String error, int maxAttempts,
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
        String lastError = truncate(error, MAX_ERROR_LENGTH);
        int updated = outboxMapper.reschedule(event.getId(), claimToken, status, attempts, next,
                lastError, now);
        if (updated != 1) {
            throw new OutboxClaimLostException("outbox publisher claim was lost before reschedule");
        }
        event.setStatus(status).setAttemptCount(attempts).setNextAttemptAt(next)
                .setClaimedAt(null).setClaimToken(null).setClaimedBy(null)
                .setLastError(lastError).setUpdatedAt(now);
        if (exhausted) {
            failAggregate(event, lastError, now);
        }
    }

    @Override
    public void releaseStaleClaims(long claimTimeoutSeconds) {
        LocalDateTime now = LocalDateTime.now();
        outboxMapper.releaseStaleClaims(now.minusSeconds(Math.max(1L, claimTimeoutSeconds)), now);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomMqOutbox replayDead(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            throw new JeecgBootException("eventId is required");
        }
        CustomMqOutbox event = outboxMapper.selectOne(new LambdaQueryWrapper<CustomMqOutbox>()
                .eq(CustomMqOutbox::getEventId, eventId));
        if (event == null || !CustomMqOutbox.STATUS_DEAD.equals(event.getStatus())) {
            throw new JeecgBootException("only a dead outbox event can be replayed");
        }
        CustomApiTask task = requireTaskMapper().selectByTaskIdForUpdate(event.getAggregateId());
        if (task == null || !CustomApiTask.STATUS_FAILED.equals(task.getStatus())
                || !"OUTBOX_DEAD".equals(task.getErrorCode())) {
            throw new JeecgBootException("task is not replayable after outbox failure");
        }
        int currentRun = task.getCustomsAiRunNo() == null ? 1 : task.getCustomsAiRunNo();
        if (event.getAggregateVersion() == null || event.getAggregateVersion() != currentRun) {
            throw new JeecgBootException("outbox replay run is no longer current");
        }
        CustomApiFile file = requireFileService().getOne(new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, task.getFileId()), false);
        if (file == null || !CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            throw new JeecgBootException("verified parse file is unavailable for replay");
        }
        LocalDateTime now = LocalDateTime.now();
        int nextRun = currentRun + 1;
        if (outboxMapper.markReplayed(event.getId(), nextRun, now) != 1) {
            throw new JeecgBootException("outbox event changed before replay");
        }
        task.setStatus(CustomApiTask.STATUS_QUEUED)
                .setStage("replay_queued")
                .setProgress(0)
                .setCustomsAiRunNo(nextRun)
                .setQueuedAt(now)
                .setLastHeartbeatAt(null)
                .setStartedAt(null)
                .setFinishedAt(null)
                .setErrorCode(null)
                .setErrorMessage(null)
                .setCallbackStatus(null)
                .setCallbackError(null);
        if (requireTaskMapper().updateById(task) != 1) {
            throw new JeecgBootException("task changed before outbox replay");
        }
        requireDocumentService().markParseQueued(task.getTaskId());
        event.setStatus(CustomMqOutbox.STATUS_REPLAYED)
                .setClaimedAt(null).setClaimToken(null).setClaimedBy(null)
                .setNextAttemptAt(null).setLastError("replayed as run " + nextRun)
                .setUpdatedAt(now);
        CustomMqOutbox replacement = enqueueParseTask(task, file, nextRun);
        log.warn("CUSTOM_MQ_OUTBOX_REPLAY oldEventId={}, taskId={}, oldRun={}, newRun={}, "
                        + "newEventId={}", event.getEventId(), task.getTaskId(), currentRun,
                nextRun, replacement.getEventId());
        return replacement;
    }

    private void failAggregate(CustomMqOutbox event, String error, LocalDateTime now) {
        CustomApiTask task = requireTaskMapper().selectByTaskIdForUpdate(event.getAggregateId());
        if (task == null) {
            throw new JeecgBootException("outbox task not found: " + event.getAggregateId());
        }
        int currentRun = task.getCustomsAiRunNo() == null ? 1 : task.getCustomsAiRunNo();
        if (event.getAggregateVersion() == null || event.getAggregateVersion() != currentRun) {
            log.warn("Ignore stale DEAD outbox aggregate transition, eventId={}, eventRun={}, currentRun={}",
                    event.getEventId(), event.getAggregateVersion(), currentRun);
            return;
        }
        if (isTerminal(task.getStatus())) {
            return;
        }
        task.setStatus(CustomApiTask.STATUS_FAILED)
                .setStage("enqueue_failed")
                .setProgress(100)
                .setErrorCode("OUTBOX_DEAD")
                .setErrorMessage(error)
                .setFinishedAt(now);
        if (shouldCallback(task)) {
            task.setCallbackStatus("pending").setCallbackError(null);
        }
        if (requireTaskMapper().updateById(task) != 1) {
            throw new JeecgBootException("task changed while failing dead outbox aggregate");
        }
        requireDocumentService().failParse(task.getTaskId(), error);
        if (shouldCallback(task)) {
            requireCallbackService().enqueueTerminal(
                    task, "task.failed", null, "OUTBOX_DEAD", error);
        }
        log.error("CUSTOM_MQ_OUTBOX_DEAD eventId={}, taskId={}, attempts={}, error={}",
                event.getEventId(), task.getTaskId(), event.getAttemptCount(), error);
    }

    private boolean isTerminal(String status) {
        return List.of(CustomApiTask.STATUS_SUCCEEDED, CustomApiTask.STATUS_FAILED,
                CustomApiTask.STATUS_CANCELLED, CustomApiTask.STATUS_TIMEOUT).contains(status);
    }

    private boolean shouldCallback(CustomApiTask task) {
        return task.getCallbackUrl() != null && !task.getCallbackUrl().isBlank()
                && !"polling".equals(task.getResponseMode());
    }

    private CustomApiTaskMapper requireTaskMapper() {
        if (taskMapper == null) {
            throw new IllegalStateException("task mapper is required for outbox state transition");
        }
        return taskMapper;
    }

    private IDocumentService requireDocumentService() {
        if (documentService == null) {
            throw new IllegalStateException("document service is required for outbox state transition");
        }
        return documentService;
    }

    private ICustomCallbackDeliveryService requireCallbackService() {
        if (callbackDeliveryService == null) {
            throw new IllegalStateException("callback service is required for outbox state transition");
        }
        return callbackDeliveryService;
    }

    private org.jeecg.modules.custom.api.service.ICustomApiFileService requireFileService() {
        if (fileService == null) {
            throw new IllegalStateException("file service is required for outbox replay");
        }
        return fileService;
    }

    private CustomMqOutbox findAggregate(String taskId, int runNo) {
        return outboxMapper.selectOne(new LambdaQueryWrapper<CustomMqOutbox>()
                .eq(CustomMqOutbox::getAggregateType, AGGREGATE_TYPE_TASK)
                .eq(CustomMqOutbox::getAggregateId, taskId)
                .eq(CustomMqOutbox::getAggregateVersion, runNo)
                .eq(CustomMqOutbox::getEventType, EVENT_TYPE_PARSE_REQUESTED));
    }

    private String buildPayload(String eventId, CustomApiTask task, CustomApiFile file,
                                int runNo, boolean includeDownloadGrant) {
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
        if (includeDownloadGrant) {
            body.put("downloadUrl", downloadTokenService.issue(
                    task.getTaskId(), file.getFileId(), runNo).url());
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

}
