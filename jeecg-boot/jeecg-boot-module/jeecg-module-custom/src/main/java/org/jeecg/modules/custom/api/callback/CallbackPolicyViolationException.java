package org.jeecg.modules.custom.api.callback;

import org.jeecg.common.exception.JeecgBootException;

public class CallbackPolicyViolationException extends JeecgBootException {
    public CallbackPolicyViolationException(String message) {
        super(message);
    }
}
