package org.jeecg.modules.custom.api.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CallbackSecretCipher {
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_BITS = 128;

    private final String activeVersion;
    private final Map<String, SecretKeySpec> keys;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public CallbackSecretCipher(
            @Value("${custom.api.callback.active-key-version:v1}") String activeVersion,
            @Value("${custom.api.callback.encryption-keys:}") String keySpec) {
        this.activeVersion = activeVersion == null ? "" : activeVersion.trim();
        this.keys = parseKeys(keySpec);
        if (!keys.containsKey(this.activeVersion)) {
            throw new IllegalStateException("callback active key version is not configured: " + this.activeVersion);
        }
    }

    public EncryptedSecret encrypt(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("callback secret is required");
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keys.get(activeVersion), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));
            byte[] envelope = ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv).put(encrypted).array();
            return new EncryptedSecret(Base64.getEncoder().encodeToString(envelope), activeVersion);
        } catch (Exception e) {
            throw new IllegalStateException("encrypt callback secret failed", e);
        }
    }

    public String decrypt(String ciphertext, String keyVersion) {
        SecretKeySpec key = keys.get(keyVersion);
        if (key == null) {
            throw new IllegalStateException("unknown callback key version: " + keyVersion);
        }
        try {
            byte[] envelope = Base64.getDecoder().decode(ciphertext);
            if (envelope.length <= IV_LENGTH) {
                throw new IllegalArgumentException("invalid callback secret ciphertext");
            }
            byte[] iv = Arrays.copyOfRange(envelope, 0, IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(envelope, IV_LENGTH, envelope.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("decrypt callback secret failed", e);
        }
    }

    private Map<String, SecretKeySpec> parseKeys(String keySpec) {
        Map<String, SecretKeySpec> result = new LinkedHashMap<>();
        if (keySpec == null || keySpec.isBlank()) {
            return result;
        }
        for (String entry : keySpec.split(",")) {
            String[] parts = entry.trim().split("=", 2);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalStateException("invalid callback encryption key entry");
            }
            byte[] key = Base64.getDecoder().decode(parts[1].trim());
            if (key.length != 16 && key.length != 24 && key.length != 32) {
                throw new IllegalStateException("callback encryption key must be 128, 192 or 256 bits");
            }
            result.put(parts[0].trim(), new SecretKeySpec(key, "AES"));
        }
        return Map.copyOf(result);
    }

    public record EncryptedSecret(String ciphertext, String keyVersion) {
    }
}
