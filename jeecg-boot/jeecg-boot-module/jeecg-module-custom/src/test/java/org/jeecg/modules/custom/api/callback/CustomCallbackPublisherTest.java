package org.jeecg.modules.custom.api.callback;

import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomCallbackPublisherTest {

    @Test
    void signsTimestampAndImmutableRawBodyThenMarksAny2xxSucceeded() throws Exception {
        Fixture fixture = fixture(new CallbackHttpResponse(204, Map.of(), ""));

        fixture.publisher.publishPending();

        ArgumentCaptor<Map<String, String>> headers = ArgumentCaptor.forClass(Map.class);
        verify(fixture.transport).send(any(),
                eq(fixture.delivery.getPayloadJson().getBytes(StandardCharsets.UTF_8)), headers.capture());
        assertThat(headers.getValue().get("X-CustomsAI-Delivery-Id")).isEqualTo("delivery-1");
        assertThat(headers.getValue().get("X-CustomsAI-Task-Id")).isEqualTo("task-1");
        assertThat(headers.getValue().get("X-CustomsAI-Timestamp")).isEqualTo("1784023200");
        assertThat(headers.getValue().get("X-CustomsAI-Signature"))
                .isEqualTo("v1=c2332d4a52bc2189c799a5ef4d092eaf6e7c2259c7c2e94f787a05499f0978c5");
        verify(fixture.service).markSucceeded(fixture.delivery, 204);
    }

    @Test
    void retries500AndHonorsBoundedRetryAfterFor429() throws Exception {
        Fixture serverError = fixture(new CallbackHttpResponse(500, Map.of(), "temporary"));
        serverError.publisher.publishPending();
        verify(serverError.service).scheduleRetry(
                eq(serverError.delivery), eq(500), any(), eq(Duration.ofMinutes(1)));

        Fixture throttled = fixture(new CallbackHttpResponse(
                429, Map.of("Retry-After", List.of("120")), "slow down"));
        throttled.publisher.publishPending();
        verify(throttled.service).scheduleRetry(
                eq(throttled.delivery), eq(429), any(), eq(Duration.ofSeconds(120)));
    }

    @Test
    void treatsOrdinary400AsPermanentFailure() throws Exception {
        Fixture fixture = fixture(new CallbackHttpResponse(400, Map.of(), "bad request"));

        fixture.publisher.publishPending();

        verify(fixture.service).markPermanentFailure(eq(fixture.delivery), eq(400), any());
        verify(fixture.service, never()).scheduleRetry(any(), anyInt(), any(), any());
    }

    @Test
    void rechecksDnsBeforeEverySendAndBlocksRebindingToPrivateAddress() throws Exception {
        AtomicInteger resolutions = new AtomicInteger();
        CallbackUrlPolicy policy = new CallbackUrlPolicy(true, host -> List.of(InetAddress.getByName(
                resolutions.getAndIncrement() == 0 ? "93.184.216.34" : "10.0.0.8")), Set.of());
        policy.validate("https://callbacks.example/result");
        Fixture fixture = fixture(new CallbackHttpResponse(200, Map.of(), ""), policy);

        fixture.publisher.publishPending();

        verify(fixture.transport, never()).send(any(), any(), any());
        verify(fixture.service).markPermanentFailure(eq(fixture.delivery), eq(null), any());
    }

    private Fixture fixture(CallbackHttpResponse response) throws Exception {
        CallbackUrlPolicy policy = new CallbackUrlPolicy(true,
                host -> List.of(InetAddress.getByName("93.184.216.34")), Set.of());
        return fixture(response, policy);
    }

    private Fixture fixture(CallbackHttpResponse response, CallbackUrlPolicy policy) throws Exception {
        ICustomCallbackDeliveryService service = mock(ICustomCallbackDeliveryService.class);
        CallbackHttpTransport transport = mock(CallbackHttpTransport.class);
        String key = Base64.getEncoder().encodeToString(new byte[32]);
        CallbackSecretCipher cipher = new CallbackSecretCipher("v1", "v1=" + key);
        CallbackSecretCipher.EncryptedSecret encrypted = cipher.encrypt("customer-secret");
        CustomCallbackDelivery delivery = new CustomCallbackDelivery()
                .setId(1L).setDeliveryId("delivery-1").setTaskId("task-1")
                .setCallbackUrl("https://callbacks.example/result")
                .setSecretCiphertext(encrypted.ciphertext()).setSecretKeyVersion(encrypted.keyVersion())
                .setPayloadJson("{\"deliveryId\":\"delivery-1\",\"status\":\"succeeded\"}")
                .setStatus(CustomCallbackDelivery.STATUS_PENDING).setAttemptCount(0);
        when(service.findDue(20)).thenReturn(List.of(delivery));
        when(service.claim(1L)).thenReturn(true);
        when(transport.send(any(), any(), any())).thenReturn(response);
        Clock clock = Clock.fixed(Instant.parse("2026-07-14T10:00:00Z"), ZoneOffset.UTC);
        CustomCallbackPublisher publisher = new CustomCallbackPublisher(
                service, policy, cipher, transport, clock, 20, 300L);
        return new Fixture(service, transport, delivery, publisher);
    }

    private record Fixture(ICustomCallbackDeliveryService service,
                           CallbackHttpTransport transport,
                           CustomCallbackDelivery delivery,
                           CustomCallbackPublisher publisher) {
    }
}
