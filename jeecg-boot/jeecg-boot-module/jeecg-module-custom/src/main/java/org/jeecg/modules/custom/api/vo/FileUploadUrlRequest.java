package org.jeecg.modules.custom.api.vo;

import lombok.Data;

@Data
public class FileUploadUrlRequest {
    private String filename;
    private String contentType;
    private Long fileSize;
    private String sha256;
    private String clientFileId;
    private String idempotencyKey;
}
