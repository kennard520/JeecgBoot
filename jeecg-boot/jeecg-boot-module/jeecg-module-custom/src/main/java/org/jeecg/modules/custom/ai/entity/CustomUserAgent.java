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
@TableName("CUSTOM_USER_AGENT")
public class CustomUserAgent implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("customer_code")
    private String customerCode;
    @TableField("user_id")
    private String userId;
    @TableField("agent_code")
    private String agentCode;
    @TableField("is_default")
    private Integer isDefault;
    @TableField("enabled")
    private Integer enabled;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
