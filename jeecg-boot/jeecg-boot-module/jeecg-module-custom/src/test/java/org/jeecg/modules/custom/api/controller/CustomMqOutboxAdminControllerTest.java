package org.jeecg.modules.custom.api.controller;

import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomMqOutboxAdminControllerTest {

    @Test
    void superAdminCanReplayDeadEventThroughControlledEndpoint() {
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        ICustomMqOutboxService outboxService = mock(ICustomMqOutboxService.class);
        CustomMqOutbox replayed = new CustomMqOutbox().setEventId("event-1")
                .setStatus(CustomMqOutbox.STATUS_PENDING);
        when(outboxService.replayDead("event-1")).thenReturn(replayed);
        CustomMqOutboxAdminController controller =
                new CustomMqOutboxAdminController(accessService, outboxService);

        var result = controller.replay("event-1");

        var ordered = inOrder(accessService, outboxService);
        ordered.verify(accessService).requireSuperAdmin();
        ordered.verify(outboxService).replayDead("event-1");
        assertThat(result.getResult()).isSameAs(replayed);
    }
}
