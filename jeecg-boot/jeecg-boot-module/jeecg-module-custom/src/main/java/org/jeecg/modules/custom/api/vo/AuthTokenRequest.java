package org.jeecg.modules.custom.api.vo;

import lombok.Data;

@Data
public class AuthTokenRequest {
    private String appKey;
    private String appSecret;
}
