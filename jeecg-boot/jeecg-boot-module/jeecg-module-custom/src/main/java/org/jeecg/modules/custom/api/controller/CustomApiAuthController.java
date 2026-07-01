package org.jeecg.modules.custom.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.jeecg.modules.custom.api.vo.AuthTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customs AI external auth")
@RestController
@RequestMapping("/custom/api/auth")
public class CustomApiAuthController {

    @Autowired
    private ICustomApiAppService appService;

    @IgnoreAuth
    @Operation(summary = "Get external API access token")
    @PostMapping("/token")
    public Result<AuthTokenResponse> token(@RequestBody AuthTokenRequest request) {
        return Result.OK(appService.issueToken(request));
    }
}
