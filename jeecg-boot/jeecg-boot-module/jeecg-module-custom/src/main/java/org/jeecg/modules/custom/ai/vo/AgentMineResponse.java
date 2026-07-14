package org.jeecg.modules.custom.ai.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AgentMineResponse {
    private CurrentCustomer customer;
    private List<AgentOptionResponse> agents;
}
