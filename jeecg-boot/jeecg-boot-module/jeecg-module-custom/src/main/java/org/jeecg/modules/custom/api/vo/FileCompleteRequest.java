package org.jeecg.modules.custom.api.vo;

import lombok.Data;

@Data
public class FileCompleteRequest {
    private Long fileSize;
    private String sha256;
    private String etag;
}
