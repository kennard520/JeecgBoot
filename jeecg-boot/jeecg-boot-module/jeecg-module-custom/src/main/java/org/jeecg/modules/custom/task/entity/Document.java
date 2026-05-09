package org.jeecg.modules.custom.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "文档")
@TableName("DOCUMENTS")
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_NOT_STARTED = DocumentStatus.NOT_STARTED.getCode();
    public static final String STATUS_PARSING = DocumentStatus.PARSING.getCode();
    public static final String STATUS_COMPLETED = DocumentStatus.COMPLETED.getCode();

    /**
     * 兼容旧调用语义：文件上传后即进入“未开始”状态，解析完成后进入“已完成”状态。
     */
    public static final String STATUS_UPLOADED = STATUS_NOT_STARTED;
    public static final String STATUS_PARSED = STATUS_COMPLETED;

    public enum DocumentStatus {
        NOT_STARTED("NOT_STARTED", "未开始"),
        PARSING("PARSING", "解析中"),
        COMPLETED("COMPLETED", "已完成");

        private final String code;
        private final String text;

        DocumentStatus(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;

    @TableField("original_filename")
    @Excel(name = "原始文件名称", width = 200)
    @Schema(description = "原始文件名称")
    private String originalFilename;

    @TableField("filename")
    @Excel(name = "文件名称", width = 200)
    @Schema(description = "文件名称")
    private String filename;

    @TableField("storage_path")
    @Excel(name = "存储路径", width = 200)
    @Schema(description = "存储路径")
    private String storagePath;

    @TableField("storage_type")
    @Excel(name = "存储类型", width = 30)
    @Schema(description = "存储类型")
    private String storageType;

    @TableField("file_size")
    @Excel(name = "文件大小", width = 20)
    @Schema(description = "文件大小")
    private Long fileSize;

    @TableField("content_type")
    @Excel(name = "文件MIME类型", width = 80)
    @Schema(description = "文件MIME类型")
    private String contentType;

    @TableField("status")
    @Excel(name = "处理状态", width = 30)
    @Schema(description = "处理状态：NOT_STARTED 未开始，PARSING 解析中，COMPLETED 已完成")
    private String status;

    @TableField("uploaded_at")
    @Excel(name = "上传时间", width = 30)
    @Schema(description = "上传时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;

    @TableField("started_at")
    @Excel(name = "开始解析时间", width = 30)
    @Schema(description = "开始解析时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @TableField("finished_at")
    @Excel(name = "解析完成时间", width = 30)
    @Schema(description = "解析完成时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishedAt;

    @TableField("dec_head_id")
    @Excel(name = "报关单id")
    @Schema(description = "报关单id")
    private Long decHeadId;

    @TableField("task_id")
    @Excel(name = "解析任务id")
    @Schema(description = "解析任务id")
    private String taskId;

    @TableField("error_message")
    @Excel(name = "错误信息", width = 200)
    @Schema(description = "错误信息")
    private String errorMessage;

    public void markUploaded(String originalFilename, String storagePath, String storageType, Long fileSize, String contentType) {
        this.originalFilename = originalFilename;
        this.filename = resolveStoredFilename(storagePath);
        this.storagePath = storagePath;
        this.storageType = storageType;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadedAt = LocalDateTime.now();
        this.status = STATUS_NOT_STARTED;
        this.errorMessage = null;
    }

    public void markParseStarted(String taskId) {
        this.taskId = taskId;
        this.startedAt = LocalDateTime.now();
        this.finishedAt = null;
        this.status = STATUS_PARSING;
        this.errorMessage = null;
    }

    public void markParsed(Long decHeadId) {
        this.decHeadId = decHeadId;
        this.finishedAt = LocalDateTime.now();
        this.status = STATUS_COMPLETED;
        this.errorMessage = null;
    }

    public void markFailed(String errorMessage) {
        this.finishedAt = LocalDateTime.now();
        this.status = STATUS_NOT_STARTED;
        this.errorMessage = errorMessage;
    }

    private String resolveStoredFilename(String storagePath) {
        if (storagePath == null || storagePath.isEmpty()) {
            return storagePath;
        }
        int index = Math.max(storagePath.lastIndexOf('/'), storagePath.lastIndexOf('\\'));
        return index >= 0 ? storagePath.substring(index + 1) : storagePath;
    }

    @Schema(description = "单一窗口查看路径")
    public String getSingleWindowPath() {
        return decHeadId == null ? null : "/custom/cit/single-window?decHeadId=" + decHeadId;
    }
}
