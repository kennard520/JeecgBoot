package org.jeecg.modules.custom.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileInfoResponse {
    private String fileId;
    private String status;
    private String filename;
    private Long fileSize;
}
