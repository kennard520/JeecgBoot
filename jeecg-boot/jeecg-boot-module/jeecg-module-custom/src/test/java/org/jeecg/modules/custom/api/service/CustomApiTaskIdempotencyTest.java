package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.exception.CustomApiConflictException;
import org.jeecg.modules.custom.api.mapper.CustomApiFileMapper;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomApiTaskIdempotencyTest {

    private final CustomApiFileMapper fileMapper = mock(CustomApiFileMapper.class);
    private final CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
    private final CustomApiIdempotencyService service = new CustomApiIdempotencyService(fileMapper, taskMapper);

    @Test
    void sameTaskKeyAndHashReturnsExistingTask() {
        CustomApiTask existing = new CustomApiTask()
                .setAppId(7L)
                .setTaskId("task-1")
                .setRequestHash("hash-1");
        when(taskMapper.selectOne(any())).thenReturn(existing);

        assertThat(service.findTask(7L, "client-1", null, "hash-1"))
                .isSameAs(existing);
    }

    @Test
    void sameTaskKeyWithDifferentHashIsConflict() {
        CustomApiTask existing = new CustomApiTask()
                .setAppId(7L)
                .setTaskId("task-1")
                .setRequestHash("hash-1");
        when(taskMapper.selectOne(any())).thenReturn(existing);

        assertThatThrownBy(() -> service.findTask(7L, "client-1", null, "hash-2"))
                .isInstanceOf(CustomApiConflictException.class)
                .hasMessageContaining("idempotency");
    }
}
