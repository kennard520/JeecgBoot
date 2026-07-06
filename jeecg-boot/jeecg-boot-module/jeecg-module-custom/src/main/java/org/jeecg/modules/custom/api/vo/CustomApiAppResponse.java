package org.jeecg.modules.custom.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CustomApiAppResponse {
    private Long id;
    private String appKey;
    private String customerCode;
    private String companyCode;
    private Integer enabled;
    private Integer rateLimit;
    private Boolean hasAccessToken;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tokenExpireAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static CustomApiAppResponse fromEntity(CustomApiApp app) {
        if (app == null) {
            return null;
        }
        return new CustomApiAppResponse()
                .setId(app.getId())
                .setAppKey(app.getAppKey())
                .setCustomerCode(app.getCustomerCode())
                .setCompanyCode(app.getCompanyCode())
                .setEnabled(app.getEnabled())
                .setRateLimit(app.getRateLimit())
                .setHasAccessToken(app.getAccessTokenHash() != null && !app.getAccessTokenHash().isBlank())
                .setTokenExpireAt(app.getTokenExpireAt())
                .setCreatedAt(app.getCreatedAt())
                .setUpdatedAt(app.getUpdatedAt());
    }
}
