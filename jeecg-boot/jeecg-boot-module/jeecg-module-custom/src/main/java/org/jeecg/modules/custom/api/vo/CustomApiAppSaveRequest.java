package org.jeecg.modules.custom.api.vo;

import lombok.Data;

@Data
public class CustomApiAppSaveRequest {
    private Long id;
    private String appKey;
    private String customerCode;
    private String companyCode;
    private Integer enabled;
    private Integer rateLimit;
}
