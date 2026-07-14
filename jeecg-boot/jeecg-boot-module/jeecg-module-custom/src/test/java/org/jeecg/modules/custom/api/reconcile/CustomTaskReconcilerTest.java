package org.jeecg.modules.custom.api.reconcile;

import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomTaskReconcilerTest {
    private static final Instant NOW = Instant.parse("2026-07-14T05:00:00Z");

    @Test
    void schedulerAsksSqlForOnlyExpiredCandidatesBeforeApplyingBatchLimit() {
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        CustomTaskReconcileService ticketService = mock(CustomTaskReconcileService.class);
        LocalDateTime now = LocalDateTime.ofInstant(NOW, ZoneOffset.UTC);
        when(taskMapper.selectStaleCandidateTaskIds(
                eq(now.minusSeconds(3600)),
                eq(now.minusSeconds(300)),
                eq(now.minusSeconds(600)),
                eq(100)))
                .thenReturn(List.of("task-101", "task-202"));
        CustomTaskReconciler reconciler = new CustomTaskReconciler(
                taskMapper, ticketService, 100, 300L, 600L, 3600L,
                Clock.fixed(NOW, ZoneOffset.UTC));

        reconciler.reconcileStaleTasks();

        verify(taskMapper).selectStaleCandidateTaskIds(
                now.minusSeconds(3600),
                now.minusSeconds(300),
                now.minusSeconds(600),
                100);
        verify(ticketService).reconcile("task-101");
        verify(ticketService).reconcile("task-202");
    }

    @Test
    void schedulerDoesNotWrapWholeBatchInOneTransaction() throws Exception {
        Transactional transactional = CustomTaskReconciler.class
                .getMethod("reconcileStaleTasks")
                .getAnnotation(Transactional.class);

        assertThat(transactional).isNull();
    }
}
