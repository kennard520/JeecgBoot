package org.jeecg.modules.custom.api.callback;

import org.jeecg.common.exception.JeecgBootException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CallbackUrlPolicyTest {

    @Test
    void acceptsHttpsHostOnlyWhenEveryResolvedAddressIsPublic() throws Exception {
        CallbackUrlPolicy policy = policy("93.184.216.34", Set.of("callbacks.example:443"));

        URI uri = policy.validate("https://callbacks.example/result");

        assertThat(uri.getHost()).isEqualTo("callbacks.example");
    }

    @Test
    void rejectsHttpCredentialsLoopbackPrivateLinkLocalAndMetadata() throws Exception {
        assertThatThrownBy(() -> policy("93.184.216.34", Set.of())
                .validate("http://callbacks.example/result"))
                .isInstanceOf(JeecgBootException.class).hasMessageContaining("HTTPS");
        assertThatThrownBy(() -> policy("93.184.216.34", Set.of())
                .validate("https://user:pass@callbacks.example/result"))
                .isInstanceOf(JeecgBootException.class).hasMessageContaining("credentials");
        for (String blocked : List.of("127.0.0.1", "10.0.0.8", "172.16.1.1",
                "192.168.1.10", "169.254.169.254", "224.0.0.1")) {
            assertThatThrownBy(() -> policy(blocked, Set.of()).validate("https://callbacks.example/result"))
                    .as(blocked)
                    .isInstanceOf(JeecgBootException.class)
                    .hasMessageContaining("public");
        }
        assertThatThrownBy(() -> policy("93.184.216.34", Set.of())
                .validate("https://metadata.google.internal/result"))
                .isInstanceOf(JeecgBootException.class).hasMessageContaining("metadata");
    }

    @Test
    void rejectsAnyPrivateAddressFromMultiAddressDnsResponse() throws Exception {
        CallbackUrlPolicy policy = new CallbackUrlPolicy(true,
                host -> List.of(InetAddress.getByName("93.184.216.34"), InetAddress.getByName("10.0.0.5")),
                Set.of());

        assertThatThrownBy(() -> policy.validate("https://callbacks.example/result"))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("public");
    }

    @Test
    void rejectsRedirectToDifferentOrPrivateHost() throws Exception {
        CallbackUrlPolicy policy = policy("93.184.216.34", Set.of());
        URI source = policy.validate("https://callbacks.example/result");

        assertThatThrownBy(() -> policy.validateRedirect(source, "https://other.example/result"))
                .isInstanceOf(JeecgBootException.class).hasMessageContaining("redirect host");
        assertThatThrownBy(() -> policy.validateRedirect(source, "https://127.0.0.1/result"))
                .isInstanceOf(JeecgBootException.class);
    }

    private CallbackUrlPolicy policy(String address, Set<String> allowlist) throws Exception {
        InetAddress resolved = InetAddress.getByName(address);
        return new CallbackUrlPolicy(true, host -> List.of(resolved), allowlist);
    }
}
