package org.jeecg.modules.custom.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class FileDownloadInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url;
    private Map<String, String> headers = new LinkedHashMap<>();
}
