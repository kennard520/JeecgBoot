package org.jeecg.modules.custom.api.callback;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdkCallbackHttpTransportTest {

    @Test
    void startsAsASpringComponentWithConfiguredTimeouts() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(JdkCallbackHttpTransport.class);
            context.refresh();

            assertThat(context.getBean(CallbackHttpTransport.class))
                    .isInstanceOf(JdkCallbackHttpTransport.class);
        }
    }

    @Test
    void pinsValidatedAddressesWhileRequestKeepsOriginalHostname() throws Exception {
        AtomicReference<String> requestHost = new AtomicReference<>();
        Interceptor capture = chain -> {
            requestHost.set(chain.request().url().host());
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(204)
                    .message("No Content")
                    .body(ResponseBody.create(null, new byte[0]))
                    .build();
        };
        JdkCallbackHttpTransport transport = new JdkCallbackHttpTransport(
                new OkHttpClient.Builder().addInterceptor(capture).build(), Duration.ofSeconds(1));
        InetAddress pinned = InetAddress.getByName("93.184.216.34");
        ValidatedCallbackTarget target = new ValidatedCallbackTarget(
                URI.create("https://callbacks.example/result"), List.of(pinned));

        CallbackHttpResponse response = transport.send(
                target, "{}".getBytes(), Map.of("Content-Type", "application/json"));

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(requestHost.get()).isEqualTo("callbacks.example");
        assertThat(transport.clientFor(target).dns().lookup("callbacks.example"))
                .containsExactly(pinned);
        assertThatThrownBy(() -> transport.clientFor(target).dns().lookup("other.example"))
                .isInstanceOf(UnknownHostException.class);
    }

    @Test
    void capsUntrustedCallbackResponseBodiesAt64KiB() throws Exception {
        byte[] oversized = new byte[128 * 1024];
        java.util.Arrays.fill(oversized, (byte) 'x');
        Interceptor response = chain -> new Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("Server Error")
                .body(ResponseBody.create(null, oversized))
                .build();
        JdkCallbackHttpTransport transport = new JdkCallbackHttpTransport(
                new OkHttpClient.Builder().addInterceptor(response).build(), Duration.ofSeconds(1));
        ValidatedCallbackTarget target = new ValidatedCallbackTarget(
                URI.create("https://callbacks.example/result"),
                List.of(InetAddress.getByName("93.184.216.34")));

        CallbackHttpResponse result = transport.send(target, "{}".getBytes(), Map.of());

        assertThat(result.body().getBytes(java.nio.charset.StandardCharsets.UTF_8))
                .hasSize(64 * 1024);
    }
}
