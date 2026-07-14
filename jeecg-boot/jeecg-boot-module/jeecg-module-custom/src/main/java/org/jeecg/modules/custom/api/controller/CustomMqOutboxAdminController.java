package org.jeecg.modules.custom.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customs AI reliability administration")
@RestController
@RequestMapping("/custom/api/admin/outbox")
public class CustomMqOutboxAdminController {
    private final CustomAgentAccessService accessService;
    private final ICustomMqOutboxService outboxService;

    @Autowired
    public CustomMqOutboxAdminController(CustomAgentAccessService accessService,
                                         ICustomMqOutboxService outboxService) {
        this.accessService = accessService;
        this.outboxService = outboxService;
    }

    @Operation(summary = "Replay a dead MQ outbox event")
    @PostMapping("/{eventId}/replay")
    public Result<CustomMqOutbox> replay(@PathVariable String eventId) {
        accessService.requireSuperAdmin();
        return Result.OK(outboxService.replayDead(eventId));
    }
}
