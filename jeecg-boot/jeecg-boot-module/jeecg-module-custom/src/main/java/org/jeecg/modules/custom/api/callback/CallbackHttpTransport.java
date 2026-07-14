package org.jeecg.modules.custom.api.callback;

import java.net.URI;
import java.util.Map;

public interface CallbackHttpTransport {
    CallbackHttpResponse send(URI uri, byte[] body, Map<String, String> headers) throws Exception;
}
