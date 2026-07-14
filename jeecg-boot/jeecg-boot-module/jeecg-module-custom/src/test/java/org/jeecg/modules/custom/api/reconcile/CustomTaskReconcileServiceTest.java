package org.jeecg.modules.custom.api.reconcile;

import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomTaskReconcileServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-14T05:00:00Z");

    @Test
    void totalTimeoutWinsEvenWhenRunningHeartbeatIsFresh() {
        CustomApiTask task = running(1)
                .setCreatedAt(time(61))
                .setLastHeartbeatAt(time(1));
        Fixture fixture = fixture(task);

        fixture.service.reconcile("task-1");

        assertThat(task.getStatus()).isEqualTo(CustomApiTask.STATUS_TIMEOUT);
        assertThat(task.getErrorCode()).isEqualTo("TASK_TOTAL_TIMEOUT");
        verify(fixture.outbox, never()).enqueueParseTask(any(), any(), any(Integer.class));
        verify(fixture.documentService).failParse(eq("task-1"), any());
    }

    @Test
    void queuedSlaCreatesNextRunAfterTakingTaskRowLock() {
        CustomApiTask task = queued(1).setQueuedAt(time(6));
        Fixture fixture = fixture(task);

        fixture.service.reconcile("task-1");

        verify(fixture.taskMapper).selectByTaskIdForUpdate("task-1");
        assertThat(task.getCustomsAiRunNo()).isEqualTo(2);
        assertThat(task.getStatus()).isEqualTo(CustomApiTask.STATUS_QUEUED);
        assertThat(task.getQueuedAt()).isEqualTo(LocalDateTime.ofInstant(NOW, ZoneOffset.UTC));
        verify(fixture.outbox).enqueueParseTask(task, fixture.file, 2);
        verify(fixture.documentService).markParseQueued("task-1");
    }

    @Test
    void staleRunningHeartbeatCreatesNextRun() {
        CustomApiTask task = running(1).setLastHeartbeatAt(time(11));
        Fixture fixture = fixture(task);

        fixture.service.reconcile("task-1");

        assertThat(task.getCustomsAiRunNo()).isEqualTo(2);
        assertThat(task.getStatus()).isEqualTo(CustomApiTask.STATUS_QUEUED);
        verify(fixture.outbox).enqueueParseTask(task, fixture.file, 2);
    }

    @Test
    void freshRunningHeartbeatIsLeftAlone() {
        CustomApiTask task = running(1).setLastHeartbeatAt(time(2));
        Fixture fixture = fixture(task);

        fixture.service.reconcile("task-1");

        verify(fixture.taskMapper, never()).updateById(any(CustomApiTask.class));
        verify(fixture.outbox, never()).enqueueParseTask(any(), any(), any(Integer.class));
    }

    @Test
    void secondInstanceSeesFreshQueuedRunAndDoesNotCreateDuplicateRun() {
        CustomApiTask task = queued(1).setQueuedAt(time(6));
        Fixture fixture = fixture(task);

        fixture.service.reconcile("task-1");
        fixture.service.reconcile("task-1");

        verify(fixture.taskMapper, times(2)).selectByTaskIdForUpdate("task-1");
        verify(fixture.outbox, times(1)).enqueueParseTask(task, fixture.file, 2);
    }

    @Test
    void eachTicketUsesRequiresNewTransaction() throws Exception {
        Transactional transactional = CustomTaskReconcileService.class
                .getMethod("reconcile", String.class)
                .getAnnotation(Transactional.class);

        assertThat(transactional).isNotNull();
        assertThat(transactional.propagation()).isEqualTo(Propagation.REQUIRES_NEW);
    }

    private Fixture fixture(CustomApiTask task) {
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        ICustomApiFileService fileService = mock(ICustomApiFileService.class);
        ICustomMqOutboxService outbox = mock(ICustomMqOutboxService.class);
        IDocumentService documentService = mock(IDocumentService.class);
        ICustomCallbackDeliveryService callback = mock(ICustomCallbackDeliveryService.class);
        CustomApiFile file = new CustomApiFile().setId(3L).setFileId("file-1")
                .setStatus(CustomApiFile.STATUS_UPLOADED);
        when(taskMapper.selectByTaskIdForUpdate("task-1")).thenReturn(task);
        when(taskMapper.updateById(any(CustomApiTask.class))).thenReturn(1);
        when(fileService.getOne(any(), eq(false))).thenReturn(file);
        CustomTaskReconcileService service = new CustomTaskReconcileService(
                taskMapper, fileService, outbox, documentService, callback,
                300L, 600L, 3600L, 3,
                Clock.fixed(NOW, ZoneOffset.UTC));
        return new Fixture(taskMapper, outbox, documentService, file, service);
    }

    private CustomApiTask queued(int runNo) {
        return base(runNo).setStatus(CustomApiTask.STATUS_QUEUED).setStage("queued");
    }

    private CustomApiTask running(int runNo) {
        return base(runNo).setStatus(CustomApiTask.STATUS_RUNNING).setStage("extracting")
                .setStartedAt(time(20));
    }

    private CustomApiTask base(int runNo) {
        return new CustomApiTask().setId(2L).setTaskId("task-1").setFileId("file-1")
                .setCustomerCode("CUSTOMER-A").setCompanyCode("CUSTOMS")
                .setCustomsAiRunNo(runNo).setCreatedAt(time(20))
                .setResponseMode("polling");
    }

    private LocalDateTime time(long minutesAgo) {
        return LocalDateTime.ofInstant(NOW.minusSeconds(minutesAgo * 60), ZoneOffset.UTC);
    }

    private record Fixture(CustomApiTaskMapper taskMapper,
                           ICustomMqOutboxService outbox,
                           IDocumentService documentService,
                           CustomApiFile file,
                           CustomTaskReconcileService service) {
    }
}
