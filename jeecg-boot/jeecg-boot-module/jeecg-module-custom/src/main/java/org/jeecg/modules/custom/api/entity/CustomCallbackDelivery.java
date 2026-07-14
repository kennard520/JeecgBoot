package org.jeecg.modules.custom.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CUSTOM_CALLBACK_DELIVERY")
public class CustomCallbackDelivery implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SENDING = "SENDING";
    public static final String STATUS_SUCCEEDED = "SUCCEEDED";
    public static final String STATUS_DEAD = "DEAD";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("delivery_id")
    private String deliveryId;

    @TableField("task_id")
    private String taskId;

    @TableField("run_no")
    private Integer runNo;

    @TableField("customer_code")
    private String customerCode;

    @TableField("event_type")
    private String eventType;

    @TableField("callback_url")
    private String callbackUrl;

    @TableField("secret_ciphertext")
    private String secretCiphertext;

    @TableField("secret_key_version")
    private String secretKeyVersion;

    @TableField("payload_json")
    private String payloadJson;

    @TableField("payload_hash")
    private String payloadHash;

    @TableField("status")
    private String status;

    @TableField("attempt_count")
    private Integer attemptCount;

    @TableField("next_attempt_at")
    private LocalDateTime nextAttemptAt;

    @TableField("claimed_at")
    private LocalDateTime claimedAt;

    @TableField("claim_token")
    private String claimToken;

    @TableField("claimed_by")
    private String claimedBy;

    @TableField("last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @TableField("delivered_at")
    private LocalDateTime deliveredAt;

    @TableField("http_status")
    private Integer httpStatus;

    @TableField("last_error")
    private String lastError;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
