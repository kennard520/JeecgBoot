package org.jeecg.modules.custom.api.callback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import okhttp3.Dns;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JdkCallbackHttpTransport implements CallbackHttpTransport {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_RESPONSE_BODY_BYTES = 64 * 1024;

    private final OkHttpClient baseClient;
    private final Duration requestTimeout;

    public JdkCallbackHttpTransport(
            @Value("${custom.api.callback.connect-timeout-seconds:10}") long connectTimeoutSeconds,
            @Value("${custom.api.callback.request-timeout-seconds:30}") long requestTimeoutSeconds) {
        this(new OkHttpClient.Builder()
                        .connectTimeout(Math.max(1L, connectTimeoutSeconds), TimeUnit.SECONDS)
                        .followRedirects(false)
                        .followSslRedirects(false)
                        .build(),
                Duration.ofSeconds(Math.max(1L, requestTimeoutSeconds)));
    }

    JdkCallbackHttpTransport(OkHttpClient baseClient, Duration requestTimeout) {
        this.baseClient = baseClient;
        this.requestTimeout = requestTimeout;
    }

    @Override
    public CallbackHttpResponse send(ValidatedCallbackTarget target, byte[] body,
                                     Map<String, String> headers) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(target.uri().toString())
                .post(RequestBody.create(body, JSON));
        headers.forEach(builder::header);
        try (Response response = clientFor(target).newCall(builder.build()).execute()) {
            byte[] responseBody = response.body() == null
                    ? new byte[0]
                    : response.body().byteStream().readNBytes(MAX_RESPONSE_BODY_BYTES + 1);
            if (responseBody.length > MAX_RESPONSE_BODY_BYTES) {
                responseBody = Arrays.copyOf(responseBody, MAX_RESPONSE_BODY_BYTES);
            }
            return new CallbackHttpResponse(response.code(), response.headers().toMultimap(),
                    new String(responseBody, StandardCharsets.UTF_8));
        }
    }

    OkHttpClient clientFor(ValidatedCallbackTarget target) {
        return baseClient.newBuilder()
                .callTimeout(Math.max(1L, requestTimeout.toMillis()), TimeUnit.MILLISECONDS)
                .dns(new PinnedDns(target))
                .build();
    }

    private static final class PinnedDns implements Dns {
        private final String host;
        private final List<InetAddress> addresses;

        private PinnedDns(ValidatedCallbackTarget target) {
            this.host = normalize(target.uri().getHost());
            this.addresses = target.addresses();
        }

        @Override
        public List<InetAddress> lookup(String hostname) throws UnknownHostException {
            if (!host.equals(normalize(hostname))) {
                throw new UnknownHostException("unvalidated callback host: " + hostname);
            }
            return addresses;
        }

        private static String normalize(String value) {
            String normalized = IDN.toASCII(value).toLowerCase(Locale.ROOT);
            while (normalized.endsWith(".")) {
                normalized = normalized.substring(0, normalized.length() - 1);
            }
            return normalized;
        }
    }
}
