package org.jeecg.modules.custom.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class CustomApiAppSaveRequest {
    private Long id;
    private String appKey;
    private String customerCode;
    private String companyCode;
    private Integer enabled;
    private Integer rateLimit;
    private List<String> agentCodes;
    private String defaultAgentCode;
}
