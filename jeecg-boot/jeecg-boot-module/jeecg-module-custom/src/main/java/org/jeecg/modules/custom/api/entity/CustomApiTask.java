package org.jeecg.modules.custom.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "Customs AI parse task")
@TableName("CUSTOM_API_TASK")
public class CustomApiTask implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_QUEUED = "queued";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_SUCCEEDED = "succeeded";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_TIMEOUT = "timeout";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("app_id")
    private Long appId;

    @TableField("task_id")
    private String taskId;

    @TableField("file_id")
    private String fileId;

    @TableField("customer_code")
    private String customerCode;

    @TableField("client_task_id")
    private String clientTaskId;

    @TableField("idempotency_key")
    private String idempotencyKey;

    @TableField("request_hash")
    private String requestHash;

    @TableField("document_id")
    private Long documentId;

    @TableField("dec_head_id")
    private Long decHeadId;

    @TableField("customs_ai_job_id")
    private String customsAiJobId;

    @TableField("customs_ai_run_no")
    private Integer customsAiRunNo;

    @TableField("direction")
    private String direction;

    @TableField("company_code")
    private String companyCode;

    @TableField("callback_url")
    private String callbackUrl;

    @TableField("callback_secret")
    private String callbackSecret;

    @TableField("callback_secret_ciphertext")
    private String callbackSecretCiphertext;

    @TableField("callback_secret_key_version")
    private String callbackSecretKeyVersion;

    @TableField("response_mode")
    private String responseMode;

    @TableField("status")
    private String status;

    @TableField("stage")
    private String stage;

    @TableField("progress")
    private Integer progress;

    @TableField("metadata_json")
    private String metadataJson;

    @TableField("result_json")
    private String resultJson;

    @TableField("error_code")
    private String errorCode;

    @TableField("error_message")
    private String errorMessage;

    @TableField("last_heartbeat_at")
    private LocalDateTime lastHeartbeatAt;

    @Version
    @TableField("version")
    private Integer version;

    @TableField("callback_status")
    private String callbackStatus;

    @TableField("callback_error")
    private String callbackError;

    @TableField("created_at")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("started_at")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @TableField("finished_at")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishedAt;
}
