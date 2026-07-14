package org.jeecg.modules.custom.api.service;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.callback.CallbackSecretCipher;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mapper.CustomCallbackDeliveryMapper;
import org.jeecg.modules.custom.api.service.impl.CustomCallbackDeliveryServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomCallbackDeliveryCasServiceTest {

    @Test
    void claimReturnsOpaqueTokenAndRecordsPublisherIdentity() {
        Fixture fixture = fixture();
        when(fixture.deliveryMapper.claim(eq(7L), any(), eq("java-a"), any())).thenReturn(1);

        String token = fixture.service.claim(7L, "java-a");

        assertThat(token).isNotBlank();
        ArgumentCaptor<String> captured = ArgumentCaptor.forClass(String.class);
        verify(fixture.deliveryMapper).claim(eq(7L), captured.capture(), eq("java-a"), any());
        assertThat(captured.getValue()).isEqualTo(token);
    }

    @Test
    void successUpdatesDeliveryAndMatchingTaskRunInOneTransaction() throws Exception {
        Fixture fixture = fixture();
        CustomCallbackDelivery delivery = delivery();
        when(fixture.deliveryMapper.markSucceeded(
                eq(7L), eq("claim-1"), eq(204), any())).thenReturn(1);
        when(fixture.taskMapper.updateCallbackStatus(
                "task-1", 2, "success", null)).thenReturn(1);

        fixture.service.markSucceeded(delivery, "claim-1", 204);

        verify(fixture.deliveryMapper).markSucceeded(
                eq(7L), eq("claim-1"), eq(204), any());
        verify(fixture.taskMapper).updateCallbackStatus("task-1", 2, "success", null);
        Transactional transactional = CustomCallbackDeliveryServiceImpl.class
                .getMethod("markSucceeded", CustomCallbackDelivery.class, String.class, int.class)
                .getAnnotation(Transactional.class);
        assertThat(transactional).isNotNull();
        assertThat(transactional.rollbackFor()).contains(Exception.class);
    }

    @Test
    void lostClaimOrChangedTaskRunRejectsCompletion() {
        Fixture lostClaim = fixture();
        when(lostClaim.deliveryMapper.markSucceeded(
                eq(7L), eq("claim-1"), eq(204), any())).thenReturn(0);
        assertThatThrownBy(() -> lostClaim.service.markSucceeded(delivery(), "claim-1", 204))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("claim");

        Fixture changedRun = fixture();
        when(changedRun.deliveryMapper.markSucceeded(
                eq(7L), eq("claim-1"), eq(204), any())).thenReturn(1);
        when(changedRun.taskMapper.updateCallbackStatus(
                "task-1", 2, "success", null)).thenReturn(0);
        assertThatThrownBy(() -> changedRun.service.markSucceeded(delivery(), "claim-1", 204))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("run");
    }

    @Test
    void deadDeliveryReplayIsAtomicAndFencedToCurrentTaskRun() {
        Fixture fixture = fixture();
        CustomCallbackDelivery dead = delivery().setStatus(CustomCallbackDelivery.STATUS_DEAD);
        CustomApiTask task = new CustomApiTask().setTaskId("task-1")
                .setCustomsAiRunNo(2).setStatus(CustomApiTask.STATUS_SUCCEEDED);
        when(fixture.deliveryMapper.selectOne(any())).thenReturn(dead);
        when(fixture.taskMapper.selectByTaskIdForUpdate("task-1")).thenReturn(task);
        when(fixture.deliveryMapper.replayDead(eq(7L), any())).thenReturn(1);
        when(fixture.taskMapper.updateCallbackStatus(
                "task-1", 2, "pending", null)).thenReturn(1);

        CustomCallbackDelivery replayed = fixture.service.replayDead("delivery-1");

        assertThat(replayed.getStatus()).isEqualTo(CustomCallbackDelivery.STATUS_PENDING);
        verify(fixture.deliveryMapper).replayDead(eq(7L), any());
        verify(fixture.taskMapper).updateCallbackStatus("task-1", 2, "pending", null);
    }

    @Test
    void deadDeliveryFromOlderRunCannotBeReplayed() {
        Fixture fixture = fixture();
        when(fixture.deliveryMapper.selectOne(any()))
                .thenReturn(delivery().setStatus(CustomCallbackDelivery.STATUS_DEAD));
        when(fixture.taskMapper.selectByTaskIdForUpdate("task-1"))
                .thenReturn(new CustomApiTask().setTaskId("task-1").setCustomsAiRunNo(3));

        assertThatThrownBy(() -> fixture.service.replayDead("delivery-1"))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("run");

        verify(fixture.deliveryMapper, never()).replayDead(any(), any());
    }

    private Fixture fixture() {
        CustomCallbackDeliveryMapper deliveryMapper = mock(CustomCallbackDeliveryMapper.class);
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        String key = Base64.getEncoder().encodeToString(new byte[32]);
        CallbackSecretCipher cipher = new CallbackSecretCipher("v1", "v1=" + key);
        return new Fixture(deliveryMapper, taskMapper,
                new CustomCallbackDeliveryServiceImpl(deliveryMapper, taskMapper, cipher));
    }

    private CustomCallbackDelivery delivery() {
        return new CustomCallbackDelivery().setId(7L).setDeliveryId("delivery-1")
                .setTaskId("task-1").setRunNo(2).setAttemptCount(0)
                .setStatus(CustomCallbackDelivery.STATUS_SENDING).setClaimToken("claim-1");
    }

    private record Fixture(CustomCallbackDeliveryMapper deliveryMapper,
                           CustomApiTaskMapper taskMapper,
                           CustomCallbackDeliveryServiceImpl service) {
    }
}
