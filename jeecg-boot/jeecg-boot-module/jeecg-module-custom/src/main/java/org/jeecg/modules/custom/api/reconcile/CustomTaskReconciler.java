package org.jeecg.modules.custom.api.reconcile;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CustomTaskReconciler {
    private final CustomApiTaskMapper taskMapper;
    private final CustomTaskReconcileService ticketService;
    private final int batchSize;
    private final long queuedTimeoutSeconds;
    private final long heartbeatTimeoutSeconds;
    private final long totalTimeoutSeconds;
    private final Clock clock;

    @Autowired
    public CustomTaskReconciler(
            CustomApiTaskMapper taskMapper,
            CustomTaskReconcileService ticketService,
            @Value("${custom.api.reconcile.batch-size:100}") int batchSize,
            @Value("${custom.api.reconcile.queued-timeout-seconds:600}") long queuedTimeoutSeconds,
            @Value("${custom.api.reconcile.heartbeat-timeout-seconds:600}") long heartbeatTimeoutSeconds,
            @Value("${custom.api.reconcile.total-timeout-seconds:3600}") long totalTimeoutSeconds) {
        this(taskMapper, ticketService, batchSize, queuedTimeoutSeconds,
                heartbeatTimeoutSeconds, totalTimeoutSeconds, Clock.systemDefaultZone());
    }

    CustomTaskReconciler(
            CustomApiTaskMapper taskMapper,
            CustomTaskReconcileService ticketService,
            int batchSize,
            long queuedTimeoutSeconds,
            long heartbeatTimeoutSeconds,
            long totalTimeoutSeconds,
            Clock clock) {
        this.taskMapper = taskMapper;
        this.ticketService = ticketService;
        this.batchSize = Math.max(1, Math.min(batchSize, 1000));
        this.queuedTimeoutSeconds = Math.max(1L, queuedTimeoutSeconds);
        this.heartbeatTimeoutSeconds = Math.max(1L, heartbeatTimeoutSeconds);
        this.totalTimeoutSeconds = Math.max(1L, totalTimeoutSeconds);
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${custom.api.reconcile.interval-ms:60000}",
            scheduler = "customReliabilityTaskScheduler")
    public void reconcileStaleTasks() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<String> candidateTaskIds = taskMapper.selectStaleCandidateTaskIds(
                now.minusSeconds(totalTimeoutSeconds),
                now.minusSeconds(queuedTimeoutSeconds),
                now.minusSeconds(heartbeatTimeoutSeconds),
                batchSize);
        if (candidateTaskIds == null) {
            return;
        }
        for (String taskId : candidateTaskIds) {
            try {
                ticketService.reconcile(taskId);
            } catch (Exception error) {
                log.error("Reconcile custom parse task failed, taskId={}",
                        taskId, error);
            }
        }
    }
}
