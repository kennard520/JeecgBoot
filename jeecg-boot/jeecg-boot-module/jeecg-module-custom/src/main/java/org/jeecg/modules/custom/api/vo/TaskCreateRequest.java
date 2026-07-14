package org.jeecg.modules.custom.api.vo;

import lombok.Data;

import java.util.Map;

@Data
public class TaskCreateRequest {
    private String fileId;
    private String clientTaskId;
    private String idempotencyKey;
    private String direction;
    private String callbackUrl;
    private String callbackSecret;
    private String responseMode;
    private String companyCode;
    private Map<String, Object> metadata;
}
