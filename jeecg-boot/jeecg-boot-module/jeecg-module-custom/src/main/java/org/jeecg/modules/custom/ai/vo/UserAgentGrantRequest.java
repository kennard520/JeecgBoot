package org.jeecg.modules.custom.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserAgentGrantRequest {
    private String customerCode;
    private String userId;
    private String username;
    private List<String> agentCodes;
    private String defaultAgentCode;
}
