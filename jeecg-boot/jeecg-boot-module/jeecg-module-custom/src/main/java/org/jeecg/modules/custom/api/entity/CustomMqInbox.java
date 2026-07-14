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
@TableName("CUSTOM_MQ_INBOX")
public class CustomMqInbox implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String STATUS_IGNORED = "IGNORED";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private String eventId;

    @TableField("task_id")
    private String taskId;

    @TableField("run_no")
    private Integer runNo;

    @TableField("event_type")
    private String eventType;

    @TableField("payload_hash")
    private String payloadHash;

    @TableField("status")
    private String status;

    @TableField("error_message")
    private String errorMessage;

    @TableField("received_at")
    private LocalDateTime receivedAt;

    @TableField("processed_at")
    private LocalDateTime processedAt;
}
