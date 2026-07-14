package org.jeecg.modules.custom.api.reconcile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CustomTaskReconcileService {
    private final CustomApiTaskMapper taskMapper;
    private final ICustomApiFileService fileService;
    private final ICustomMqOutboxService outboxService;
    private final IDocumentService documentService;
    private final ICustomCallbackDeliveryService callbackDeliveryService;
    private final long queuedTimeoutSeconds;
    private final long heartbeatTimeoutSeconds;
    private final long totalTimeoutSeconds;
    private final int maxRuns;
    private final Clock clock;

    @Autowired
    public CustomTaskReconcileService(
            CustomApiTaskMapper taskMapper,
            ICustomApiFileService fileService,
            ICustomMqOutboxService outboxService,
            IDocumentService documentService,
            ICustomCallbackDeliveryService callbackDeliveryService,
            @Value("${custom.api.reconcile.queued-timeout-seconds:600}") long queuedTimeoutSeconds,
            @Value("${custom.api.reconcile.heartbeat-timeout-seconds:600}") long heartbeatTimeoutSeconds,
            @Value("${custom.api.reconcile.total-timeout-seconds:3600}") long totalTimeoutSeconds,
            @Value("${custom.api.reconcile.max-runs:3}") int maxRuns) {
        this(taskMapper, fileService, outboxService, documentService, callbackDeliveryService,
                queuedTimeoutSeconds, heartbeatTimeoutSeconds, totalTimeoutSeconds, maxRuns,
                Clock.systemDefaultZone());
    }

    public CustomTaskReconcileService(
            CustomApiTaskMapper taskMapper,
            ICustomApiFileService fileService,
            ICustomMqOutboxService outboxService,
            IDocumentService documentService,
            ICustomCallbackDeliveryService callbackDeliveryService,
            long queuedTimeoutSeconds,
            long heartbeatTimeoutSeconds,
            long totalTimeoutSeconds,
            int maxRuns,
            Clock clock) {
        this.taskMapper = taskMapper;
        this.fileService = fileService;
        this.outboxService = outboxService;
        this.documentService = documentService;
        this.callbackDeliveryService = callbackDeliveryService;
        this.queuedTimeoutSeconds = queuedTimeoutSeconds;
        this.heartbeatTimeoutSeconds = heartbeatTimeoutSeconds;
        this.totalTimeoutSeconds = totalTimeoutSeconds;
        this.maxRuns = maxRuns;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void reconcile(String taskId) {
        CustomApiTask task = taskMapper.selectByTaskIdForUpdate(taskId);
        if (task == null || !List.of(CustomApiTask.STATUS_QUEUED, CustomApiTask.STATUS_RUNNING)
                .contains(task.getStatus())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now(clock);

        if (expired(task.getCreatedAt(), totalTimeoutSeconds, now)) {
            timeout(task, "TASK_TOTAL_TIMEOUT", "Parse task exceeded total timeout", now);
            return;
        }
        if (CustomApiTask.STATUS_QUEUED.equals(task.getStatus())) {
            LocalDateTime queuedAt = firstNonNull(task.getQueuedAt(), task.getCreatedAt());
            if (!expired(queuedAt, queuedTimeoutSeconds, now)) {
                return;
            }
        } else {
            LocalDateTime activity = latest(task.getLastHeartbeatAt(), task.getStartedAt(),
                    task.getQueuedAt(), task.getCreatedAt());
            if (!expired(activity, heartbeatTimeoutSeconds, now)) {
                return;
            }
        }

        int runNo = task.getCustomsAiRunNo() == null ? 1 : task.getCustomsAiRunNo();
        if (runNo >= maxRuns) {
            timeout(task, "TASK_RETRY_EXHAUSTED", "Parse task retry limit reached", now);
            return;
        }
        CustomApiFile file = fileService.getOne(new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, task.getFileId()), false);
        if (file == null || !CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            timeout(task, "TASK_FILE_UNAVAILABLE", "Verified parse file is unavailable", now);
            return;
        }

        int nextRun = runNo + 1;
        task.setStatus(CustomApiTask.STATUS_QUEUED)
                .setStage("retry_queued")
                .setProgress(0)
                .setCustomsAiRunNo(nextRun)
                .setQueuedAt(now)
                .setLastHeartbeatAt(null)
                .setStartedAt(null)
                .setFinishedAt(null)
                .setErrorCode(null)
                .setErrorMessage(null);
        if (taskMapper.updateById(task) != 1) {
            throw new JeecgBootException("task changed while scheduling retry");
        }
        documentService.markParseQueued(task.getTaskId());
        outboxService.enqueueParseTask(task, file, nextRun);
    }

    private void timeout(CustomApiTask task, String errorCode, String errorMessage,
                         LocalDateTime now) {
        task.setStatus(CustomApiTask.STATUS_TIMEOUT)
                .setStage("timeout")
                .setProgress(100)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage)
                .setFinishedAt(now);
        if (shouldCallback(task)) {
            task.setCallbackStatus("pending").setCallbackError(null);
        }
        if (taskMapper.updateById(task) != 1) {
            throw new JeecgBootException("task changed while marking timeout");
        }
        documentService.failParse(task.getTaskId(), errorMessage);
        if (shouldCallback(task)) {
            callbackDeliveryService.enqueueTerminal(
                    task, "task.failed", null, errorCode, errorMessage);
        }
        log.warn("Custom parse task reconciled to timeout, taskId={}, errorCode={}",
                task.getTaskId(), errorCode);
    }

    private boolean shouldCallback(CustomApiTask task) {
        return task.getCallbackUrl() != null && !task.getCallbackUrl().isBlank()
                && !"polling".equals(task.getResponseMode());
    }

    private boolean expired(LocalDateTime value, long timeoutSeconds, LocalDateTime now) {
        return value != null && value.isBefore(now.minusSeconds(Math.max(1, timeoutSeconds)));
    }

    private LocalDateTime firstNonNull(LocalDateTime first, LocalDateTime second) {
        return first == null ? second : first;
    }

    private LocalDateTime latest(LocalDateTime... values) {
        LocalDateTime result = null;
        for (LocalDateTime value : values) {
            if (value != null && (result == null || value.isAfter(result))) {
                result = value;
            }
        }
        return result;
    }
}
