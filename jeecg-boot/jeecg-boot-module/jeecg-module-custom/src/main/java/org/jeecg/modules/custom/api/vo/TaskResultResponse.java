package org.jeecg.modules.custom.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskResultResponse {
    private String taskId;
    private String clientTaskId;
    private String status;
    private Object declareData;
    private Object warnings;
}
