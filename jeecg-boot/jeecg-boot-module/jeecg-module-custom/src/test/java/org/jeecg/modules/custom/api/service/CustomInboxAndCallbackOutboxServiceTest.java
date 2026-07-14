package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.callback.CallbackSecretCipher;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.entity.CustomMqInbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mapper.CustomCallbackDeliveryMapper;
import org.jeecg.modules.custom.api.mapper.CustomMqInboxMapper;
import org.jeecg.modules.custom.api.service.impl.CustomCallbackDeliveryServiceImpl;
import org.jeecg.modules.custom.api.service.impl.CustomMqInboxServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomInboxAndCallbackOutboxServiceTest {

    @Test
    void duplicateInboxEventReturnsNullWithoutAnotherInsert() {
        CustomMqInboxMapper mapper = mock(CustomMqInboxMapper.class);
        CustomMqInbox existing = new CustomMqInbox().setId(1L).setEventId("event-1");
        when(mapper.selectOne(any())).thenReturn(existing);
        CustomMqInboxServiceImpl service = new CustomMqInboxServiceImpl(mapper);

        CustomMqInbox received = service.receive(
                "event-1", "task-1", 1, "parse.status", "a".repeat(64));

        assertThat(received).isNull();
        verify(mapper, never()).insert(any(CustomMqInbox.class));
    }

    @Test
    void callbackDuplicateKeyRaceReturnsCommittedTerminalWinner() {
        CustomCallbackDeliveryMapper deliveryMapper = mock(CustomCallbackDeliveryMapper.class);
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        CallbackSecretCipher cipher = cipher();
        CustomCallbackDelivery winner = new CustomCallbackDelivery()
                .setId(9L).setDeliveryId("delivery-winner");
        when(deliveryMapper.selectOne(any())).thenReturn(null, winner);
        when(deliveryMapper.insert(any(CustomCallbackDelivery.class)))
                .thenThrow(new DuplicateKeyException("raced"));
        CustomCallbackDeliveryServiceImpl service = new CustomCallbackDeliveryServiceImpl(
                deliveryMapper, taskMapper, cipher);

        CustomCallbackDelivery delivery = service.enqueueTerminal(
                callbackTask(cipher), "task.completed", Map.of("ok", true), null, null);

        assertThat(delivery).isSameAs(winner);
        verify(deliveryMapper).insert(any(CustomCallbackDelivery.class));
    }

    @Test
    void legacyPlaintextCallbackSecretIsEncryptedAndClearedBeforeDelivery() {
        CustomCallbackDeliveryMapper deliveryMapper = mock(CustomCallbackDeliveryMapper.class);
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        CallbackSecretCipher cipher = cipher();
        when(deliveryMapper.insert(any(CustomCallbackDelivery.class))).thenReturn(1);
        CustomCallbackDeliveryServiceImpl service = new CustomCallbackDeliveryServiceImpl(
                deliveryMapper, taskMapper, cipher);
        CustomApiTask task = callbackTask(cipher)
                .setCallbackSecretCiphertext(null)
                .setCallbackSecretKeyVersion(null)
                .setCallbackSecret("legacy-secret");

        CustomCallbackDelivery delivery = service.enqueueTerminal(
                task, "task.completed", Map.of("ok", true), null, null);

        assertThat(task.getCallbackSecret()).isNull();
        assertThat(task.getCallbackSecretCiphertext()).isNotBlank();
        assertThat(delivery.getSecretCiphertext()).isEqualTo(task.getCallbackSecretCiphertext());
        verify(taskMapper).updateById(task);
    }

    private CustomApiTask callbackTask(CallbackSecretCipher cipher) {
        CallbackSecretCipher.EncryptedSecret encrypted = cipher.encrypt("customer-secret");
        return new CustomApiTask().setTaskId("task-1").setFileId("file-1")
                .setCustomerCode("CUSTOMER-A").setCustomsAiRunNo(1)
                .setStatus(CustomApiTask.STATUS_SUCCEEDED).setResponseMode("callback")
                .setCallbackUrl("https://callbacks.example/result")
                .setCallbackSecretCiphertext(encrypted.ciphertext())
                .setCallbackSecretKeyVersion(encrypted.keyVersion())
                .setFinishedAt(LocalDateTime.now());
    }

    private CallbackSecretCipher cipher() {
        String key = Base64.getEncoder().encodeToString(new byte[32]);
        return new CallbackSecretCipher("v1", "v1=" + key);
    }
}
