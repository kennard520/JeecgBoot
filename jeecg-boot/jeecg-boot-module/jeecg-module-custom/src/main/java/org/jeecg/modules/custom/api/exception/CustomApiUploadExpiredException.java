package org.jeecg.modules.custom.api.exception;

import org.jeecg.common.exception.JeecgBootException;

public class CustomApiUploadExpiredException extends JeecgBootException {
    public CustomApiUploadExpiredException(String message) {
        super(message);
    }
}
