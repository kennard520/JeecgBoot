package org.jeecg.modules.custom.api.mq;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomMqOutboxPublisherTest {

    @Test
    void ackMarksClaimedEventSent() {
        Fixture fixture = fixture();

        fixture.publisher.publishPending();

        verify(fixture.outbox).markSent(1L);
        verify(fixture.outbox, never()).reschedule(any(), any(), anyInt(), anyLong(), anyLong());
    }

    @Test
    void nackKeepsClaimedEventPendingWithRetry() {
        Fixture fixture = fixture();
        doThrow(new JeecgBootException("broker nack"))
                .when(fixture.producer).publishConfirmed(any(), any(), any());

        fixture.publisher.publishPending();

        verify(fixture.outbox, never()).markSent(any());
        verify(fixture.outbox).reschedule(eq(fixture.event), eq("broker nack"), eq(8), eq(2L), eq(300L));
    }

    private Fixture fixture() {
        ICustomMqOutboxService outbox = mock(ICustomMqOutboxService.class);
        CustomApiTaskMqProducer producer = mock(CustomApiTaskMqProducer.class);
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        ICustomApiFileService fileService = mock(ICustomApiFileService.class);
        CustomMqOutbox event = new CustomMqOutbox().setId(1L).setAggregateId("task-1")
                .setAggregateVersion(1).setStatus(CustomMqOutbox.STATUS_PENDING).setAttemptCount(0);
        CustomApiTask task = new CustomApiTask().setId(2L).setTaskId("task-1").setFileId("file-1");
        CustomApiFile file = new CustomApiFile().setId(3L).setFileId("file-1").setStatus(CustomApiFile.STATUS_UPLOADED);
        when(outbox.findPublishable(20)).thenReturn(List.of(event));
        when(outbox.claim(1L)).thenReturn(true);
        when(taskMapper.selectOne(any())).thenReturn(task);
        when(fileService.getOne(any(), eq(false))).thenReturn(file);
        CustomMqOutboxPublisher publisher = new CustomMqOutboxPublisher(
                outbox, producer, taskMapper, fileService, 20, 8, 2L, 300L, 300L);
        return new Fixture(outbox, producer, event, publisher);
    }

    private record Fixture(ICustomMqOutboxService outbox, CustomApiTaskMqProducer producer,
                           CustomMqOutbox event, CustomMqOutboxPublisher publisher) {
    }
}
