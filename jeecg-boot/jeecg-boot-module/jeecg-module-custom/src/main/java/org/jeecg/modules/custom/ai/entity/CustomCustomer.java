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
@TableName("CUSTOM_CUSTOMER")
public class CustomCustomer implements Serializable {
    @TableId(value = "customer_code", type = IdType.INPUT)
    private String customerCode;
    @TableField("customer_name")
    private String customerName;
    @TableField("enabled")
    private Integer enabled;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public boolean isEnabledCustomer() {
        return Integer.valueOf(1).equals(enabled);
    }
}
