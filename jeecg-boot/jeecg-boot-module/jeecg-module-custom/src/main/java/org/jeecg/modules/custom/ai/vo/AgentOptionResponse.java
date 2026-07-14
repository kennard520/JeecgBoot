package org.jeecg.modules.custom.ai.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AgentOptionResponse {
    private String agentCode;
    private String agentName;
    private String description;
    private Boolean defaultAgent;
}
