package org.jeecg.modules.custom.api.callback;

import java.util.Map;

public interface CallbackHttpTransport {
    CallbackHttpResponse send(ValidatedCallbackTarget target, byte[] body,
                              Map<String, String> headers) throws Exception;
}
