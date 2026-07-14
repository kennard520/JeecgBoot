package org.jeecg.modules.custom.api.callback;

import java.util.List;
import java.util.Map;

public record CallbackHttpResponse(int statusCode, Map<String, List<String>> headers, String body) {
}
