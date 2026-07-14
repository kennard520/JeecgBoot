package org.jeecg.modules.custom.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApiAppAgentGrantRequest {
    private Long appId;
    private List<String> agentCodes;
    private String defaultAgentCode;
}
