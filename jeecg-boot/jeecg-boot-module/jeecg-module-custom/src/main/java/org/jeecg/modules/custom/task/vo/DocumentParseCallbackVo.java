package org.jeecg.modules.custom.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文档解析任务回调入参。
 */
@Data
@Schema(description = "文档解析任务回调入参")
public class DocumentParseCallbackVo {

    @Schema(description = "解析任务 ID")
    private String taskId;

    @Schema(description = "报关单表头 ID")
    private Long decHeadId;

    @Schema(description = "是否解析成功")
    private Boolean success = true;

    @Schema(description = "失败原因")
    private String errorMessage;
}
