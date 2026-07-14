package org.jeecg.modules.custom.api.reconcile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomTaskReconciler {
    private final CustomApiTaskMapper taskMapper;
    private final ICustomApiFileService fileService;
    private final ICustomMqOutboxService outboxService;
    private final IDocumentService documentService;
    private final long heartbeatTimeoutSeconds;
    private final long totalTimeoutSeconds;
    private final int maxRuns;

    @Autowired
    public CustomTaskReconciler(
            CustomApiTaskMapper taskMapper,
            ICustomApiFileService fileService,
            ICustomMqOutboxService outboxService,
            IDocumentService documentService,
            @Value("${custom.api.reconcile.heartbeat-timeout-seconds:600}") long heartbeatTimeoutSeconds,
            @Value("${custom.api.reconcile.total-timeout-seconds:3600}") long totalTimeoutSeconds,
            @Value("${custom.api.reconcile.max-runs:3}") int maxRuns) {
        this.taskMapper = taskMapper;
        this.fileService = fileService;
        this.outboxService = outboxService;
        this.documentService = documentService;
        this.heartbeatTimeoutSeconds = heartbeatTimeoutSeconds;
        this.totalTimeoutSeconds = totalTimeoutSeconds;
        this.maxRuns = maxRuns;
    }

    @Scheduled(fixedDelayString = "${custom.api.reconcile.interval-ms:60000}")
    @Transactional(rollbackFor = Exception.class)
    public void reconcileStaleTasks() {
        LocalDateTime now = LocalDateTime.now();
        for (CustomApiTask task : taskMapper.selectList(new LambdaQueryWrapper<CustomApiTask>()
                .eq(CustomApiTask::getStatus, CustomApiTask.STATUS_RUNNING))) {
            if (!CustomApiTask.STATUS_RUNNING.equals(task.getStatus())) {
                continue;
            }
            LocalDateTime activity = latest(task.getLastHeartbeatAt(), task.getStartedAt(), task.getCreatedAt());
            if (activity == null || !activity.isBefore(now.minusSeconds(heartbeatTimeoutSeconds))) {
                continue;
            }
            int runNo = task.getCustomsAiRunNo() == null ? 1 : task.getCustomsAiRunNo();
            boolean totalExpired = task.getCreatedAt() != null
                    && task.getCreatedAt().isBefore(now.minusSeconds(totalTimeoutSeconds));
            if (runNo >= maxRuns || totalExpired) {
                timeout(task, now);
                continue;
            }
            CustomApiFile file = fileService.getOne(new LambdaQueryWrapper<CustomApiFile>()
                    .eq(CustomApiFile::getFileId, task.getFileId()), false);
            if (file == null || !CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
                timeout(task, now);
                continue;
            }
            int nextRun = runNo + 1;
            task.setStatus(CustomApiTask.STATUS_QUEUED)
                    .setStage("retry_queued")
                    .setProgress(0)
                    .setCustomsAiRunNo(nextRun)
                    .setLastHeartbeatAt(null)
                    .setStartedAt(null)
                    .setFinishedAt(null)
                    .setErrorCode(null)
                    .setErrorMessage(null);
            taskMapper.updateById(task);
            outboxService.enqueueParseTask(task, file, nextRun);
        }
    }

    private void timeout(CustomApiTask task, LocalDateTime now) {
        task.setStatus(CustomApiTask.STATUS_TIMEOUT)
                .setStage("timeout")
                .setErrorCode("TASK_TIMEOUT")
                .setErrorMessage("解析任务超时")
                .setFinishedAt(now);
        taskMapper.updateById(task);
        try {
            documentService.failParse(task.getTaskId(), "解析任务超时");
        } catch (Exception e) {
            log.warn("Mark timed out document failed, taskId={}", task.getTaskId(), e);
        }
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
