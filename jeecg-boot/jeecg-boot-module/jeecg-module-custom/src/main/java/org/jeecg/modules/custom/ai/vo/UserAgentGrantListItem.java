package org.jeecg.modules.custom.ai.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserAgentGrantListItem {
    private Long id;
    private String customerCode;
    private String customerName;
    private String userId;
    private String username;
    private String realname;
    private List<String> agentCodes;
    private List<String> agentNames;
    private String defaultAgentCode;
    private String defaultAgentName;
    private LocalDateTime updatedAt;
}
