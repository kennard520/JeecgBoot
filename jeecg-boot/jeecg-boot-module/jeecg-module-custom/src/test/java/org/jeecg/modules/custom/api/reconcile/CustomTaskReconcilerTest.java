package org.jeecg.modules.custom.api.reconcile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomTaskReconcilerTest {

    @Test
    void schedulerDelegatesEachCandidateToIndependentTicketService() {
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        CustomTaskReconcileService ticketService = mock(CustomTaskReconcileService.class);
        Page<CustomApiTask> page = new Page<>();
        page.setRecords(List.of(
                new CustomApiTask().setTaskId("task-1"),
                new CustomApiTask().setTaskId("task-2")));
        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(page);
        CustomTaskReconciler reconciler = new CustomTaskReconciler(taskMapper, ticketService, 100);

        reconciler.reconcileStaleTasks();

        verify(ticketService).reconcile("task-1");
        verify(ticketService).reconcile("task-2");
    }

    @Test
    void schedulerDoesNotWrapWholeBatchInOneTransaction() throws Exception {
        Transactional transactional = CustomTaskReconciler.class
                .getMethod("reconcileStaleTasks")
                .getAnnotation(Transactional.class);

        assertThat(transactional).isNull();
    }
}
