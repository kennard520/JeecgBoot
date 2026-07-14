package org.jeecg.modules.custom.api.reconcile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomTaskReconciler {
    private final CustomApiTaskMapper taskMapper;
    private final CustomTaskReconcileService ticketService;
    private final int batchSize;

    @Autowired
    public CustomTaskReconciler(
            CustomApiTaskMapper taskMapper,
            CustomTaskReconcileService ticketService,
            @Value("${custom.api.reconcile.batch-size:100}") int batchSize) {
        this.taskMapper = taskMapper;
        this.ticketService = ticketService;
        this.batchSize = Math.max(1, Math.min(batchSize, 1000));
    }

    @Scheduled(fixedDelayString = "${custom.api.reconcile.interval-ms:60000}")
    public void reconcileStaleTasks() {
        Page<CustomApiTask> page = taskMapper.selectPage(
                new Page<>(1, batchSize, false),
                new LambdaQueryWrapper<CustomApiTask>()
                        .in(CustomApiTask::getStatus,
                                CustomApiTask.STATUS_QUEUED, CustomApiTask.STATUS_RUNNING)
                        .orderByAsc(CustomApiTask::getId));
        for (CustomApiTask candidate : page.getRecords()) {
            try {
                ticketService.reconcile(candidate.getTaskId());
            } catch (Exception error) {
                log.error("Reconcile custom parse task failed, taskId={}",
                        candidate.getTaskId(), error);
            }
        }
    }
}
