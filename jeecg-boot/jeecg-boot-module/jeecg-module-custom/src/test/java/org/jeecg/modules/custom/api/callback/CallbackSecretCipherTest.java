package org.jeecg.modules.custom.api.callback;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CallbackSecretCipherTest {

    @Test
    void encryptsWithAesGcmAndRetainsKeyVersionForRotation() {
        String key = Base64.getEncoder().encodeToString(new byte[32]);
        CallbackSecretCipher cipher = new CallbackSecretCipher("v2", "v1=" + key + ",v2=" + key);

        CallbackSecretCipher.EncryptedSecret encrypted = cipher.encrypt("customer-secret");

        assertThat(encrypted.keyVersion()).isEqualTo("v2");
        assertThat(encrypted.ciphertext()).doesNotContain("customer-secret");
        assertThat(cipher.decrypt(encrypted.ciphertext(), encrypted.keyVersion()))
                .isEqualTo("customer-secret");
    }

    @Test
    void rejectsUnknownKeyVersion() {
        String key = Base64.getEncoder().encodeToString(new byte[32]);
        CallbackSecretCipher cipher = new CallbackSecretCipher("v1", "v1=" + key);

        assertThatThrownBy(() -> cipher.decrypt("invalid", "v0"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("v0");
    }
}
