package org.jeecg.modules.custom.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CustomApiAppSecretResponse extends CustomApiAppResponse {
    private String appSecret;

    public static CustomApiAppSecretResponse fromApp(CustomApiAppResponse app, String appSecret) {
        CustomApiAppSecretResponse response = new CustomApiAppSecretResponse();
        response.setAppSecret(appSecret);
        response.setId(app.getId());
        response.setAppKey(app.getAppKey());
        response.setCustomerCode(app.getCustomerCode());
        response.setCompanyCode(app.getCompanyCode());
        response.setEnabled(app.getEnabled());
        response.setRateLimit(app.getRateLimit());
        response.setHasAccessToken(app.getHasAccessToken());
        response.setTokenExpireAt(app.getTokenExpireAt());
        response.setCreatedAt(app.getCreatedAt());
        response.setUpdatedAt(app.getUpdatedAt());
        return response;
    }
}
