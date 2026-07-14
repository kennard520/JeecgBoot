package org.jeecg.modules.custom.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customs AI callback reliability administration")
@RestController
@RequestMapping("/custom/api/admin/callback")
public class CustomCallbackAdminController {
    private final CustomAgentAccessService accessService;
    private final ICustomCallbackDeliveryService deliveryService;

    public CustomCallbackAdminController(CustomAgentAccessService accessService,
                                         ICustomCallbackDeliveryService deliveryService) {
        this.accessService = accessService;
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Replay a dead callback delivery")
    @PostMapping("/{deliveryId}/replay")
    public Result<CustomCallbackDelivery> replay(@PathVariable String deliveryId) {
        accessService.requireSuperAdmin();
        return Result.OK(deliveryService.replayDead(deliveryId));
    }
}
