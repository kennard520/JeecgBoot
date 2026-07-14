package org.jeecg.modules.custom.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("CUSTOM_AI_AGENT")
public class CustomAiAgent implements Serializable {
    @TableId(value = "agent_code", type = IdType.INPUT)
    private String agentCode;
    @TableField("agent_name")
    private String agentName;
    @TableField("enabled")
    private Integer enabled;
    @TableField("sort_order")
    private Integer sortOrder;
    @TableField("description")
    private String description;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public boolean isEnabledAgent() {
        return Integer.valueOf(1).equals(enabled);
    }
}
