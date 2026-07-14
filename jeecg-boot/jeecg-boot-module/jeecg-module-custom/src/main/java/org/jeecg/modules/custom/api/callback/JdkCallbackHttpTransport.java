package org.jeecg.modules.custom.api.callback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
public class JdkCallbackHttpTransport implements CallbackHttpTransport {
    private final HttpClient client;
    private final Duration requestTimeout;

    public JdkCallbackHttpTransport(
            @Value("${custom.api.callback.connect-timeout-seconds:10}") long connectTimeoutSeconds,
            @Value("${custom.api.callback.request-timeout-seconds:30}") long requestTimeoutSeconds) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Math.max(1L, connectTimeoutSeconds)))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        this.requestTimeout = Duration.ofSeconds(Math.max(1L, requestTimeoutSeconds));
    }

    @Override
    public CallbackHttpResponse send(URI uri, byte[] body, Map<String, String> headers) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(requestTimeout)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body));
        headers.forEach(builder::header);
        HttpResponse<byte[]> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        return new CallbackHttpResponse(response.statusCode(), response.headers().map(),
                new String(response.body(), StandardCharsets.UTF_8));
    }
}
