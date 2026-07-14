package org.jeecg.modules.custom.api.controller;

import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomCallbackAdminControllerTest {

    @Test
    void onlySuperAdminCanReplayDeadCallbackDelivery() {
        CustomAgentAccessService access = mock(CustomAgentAccessService.class);
        ICustomCallbackDeliveryService service = mock(ICustomCallbackDeliveryService.class);
        CustomCallbackDelivery replayed = new CustomCallbackDelivery()
                .setDeliveryId("delivery-1").setStatus(CustomCallbackDelivery.STATUS_PENDING);
        when(service.replayDead("delivery-1")).thenReturn(replayed);
        CustomCallbackAdminController controller = new CustomCallbackAdminController(access, service);

        var result = controller.replay("delivery-1");

        var ordered = inOrder(access, service);
        ordered.verify(access).requireSuperAdmin();
        ordered.verify(service).replayDead("delivery-1");
        assertThat(result.getResult()).isSameAs(replayed);
    }
}
