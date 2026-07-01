package org.jeecg.modules.custom.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthTokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
