package org.jeecg.modules.custom.api.reconcile;

import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomTaskReconcilerTest {

    @Test
    void requeuesRunningTaskAfterTenMinutesWithoutHeartbeat() {
        Fixture fixture = fixture(staleRunning(1, 20, 11));

        fixture.reconciler.reconcileStaleTasks();

        ArgumentCaptor<CustomApiTask> updated = ArgumentCaptor.forClass(CustomApiTask.class);
        verify(fixture.taskMapper).updateById(updated.capture());
        assertThat(updated.getValue().getStatus()).isEqualTo(CustomApiTask.STATUS_QUEUED);
        assertThat(updated.getValue().getCustomsAiRunNo()).isEqualTo(2);
        verify(fixture.outbox).enqueueParseTask(eq(updated.getValue()), eq(fixture.file), eq(2));
    }

    @Test
    void marksTimeoutAfterThirdRunWithoutCreatingAnotherRequest() {
        Fixture fixture = fixture(staleRunning(3, 40, 11));

        fixture.reconciler.reconcileStaleTasks();

        ArgumentCaptor<CustomApiTask> updated = ArgumentCaptor.forClass(CustomApiTask.class);
        verify(fixture.taskMapper).updateById(updated.capture());
        assertThat(updated.getValue().getStatus()).isEqualTo(CustomApiTask.STATUS_TIMEOUT);
        verify(fixture.outbox, never()).enqueueParseTask(any(), any(), any(Integer.class));
        verify(fixture.documentService).failParse("task-1", "解析任务超时");
    }

    @Test
    void neverRepublishesConfirmedRequestMerelyBecauseTaskIsStillQueued() {
        CustomApiTask queued = staleRunning(1, 20, 11).setStatus(CustomApiTask.STATUS_QUEUED);
        Fixture fixture = fixture(queued);

        fixture.reconciler.reconcileStaleTasks();

        verify(fixture.taskMapper, never()).updateById(any(CustomApiTask.class));
        verify(fixture.outbox, never()).enqueueParseTask(any(), any(), any(Integer.class));
    }

    private Fixture fixture(CustomApiTask task) {
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        ICustomApiFileService fileService = mock(ICustomApiFileService.class);
        ICustomMqOutboxService outbox = mock(ICustomMqOutboxService.class);
        IDocumentService documentService = mock(IDocumentService.class);
        CustomApiFile file = new CustomApiFile().setId(3L).setFileId("file-1")
                .setStatus(CustomApiFile.STATUS_UPLOADED);
        when(taskMapper.selectList(any())).thenReturn(List.of(task));
        when(fileService.getOne(any(), eq(false))).thenReturn(file);
        CustomTaskReconciler reconciler = new CustomTaskReconciler(
                taskMapper, fileService, outbox, documentService, 600L, 3600L, 3);
        return new Fixture(taskMapper, outbox, documentService, file, reconciler);
    }

    private CustomApiTask staleRunning(int runNo, int ageMinutes, int heartbeatAgeMinutes) {
        LocalDateTime now = LocalDateTime.now();
        return new CustomApiTask().setId(2L).setTaskId("task-1").setFileId("file-1")
                .setStatus(CustomApiTask.STATUS_RUNNING).setStage("extracting")
                .setCustomsAiRunNo(runNo).setCreatedAt(now.minusMinutes(ageMinutes))
                .setStartedAt(now.minusMinutes(ageMinutes - 1))
                .setLastHeartbeatAt(now.minusMinutes(heartbeatAgeMinutes));
    }

    private record Fixture(CustomApiTaskMapper taskMapper, ICustomMqOutboxService outbox,
                           IDocumentService documentService, CustomApiFile file,
                           CustomTaskReconciler reconciler) {
    }
}
