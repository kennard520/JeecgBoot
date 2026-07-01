package org.jeecg.modules.custom.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class FileUploadUrlResponse {
    private String fileId;
    private String storageType;
    private String objectKey;
    private String uploadMethod;
    private String uploadUrl;
    private Map<String, String> headers;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
}
