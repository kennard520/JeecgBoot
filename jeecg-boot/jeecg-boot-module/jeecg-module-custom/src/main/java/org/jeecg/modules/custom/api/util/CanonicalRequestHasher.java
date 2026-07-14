package org.jeecg.modules.custom.api.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.vo.FileUploadUrlRequest;
import org.jeecg.modules.custom.api.vo.TaskCreateRequest;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

@Component
public class CanonicalRequestHasher {
    private final ObjectMapper mapper = JsonMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();

    public String hashFile(FileUploadUrlRequest request) {
        Map<String, Object> value = new TreeMap<>();
        value.put("clientFileId", normalize(request.getClientFileId()));
        value.put("contentType", normalizeLower(request.getContentType()));
        value.put("fileSize", request.getFileSize());
        value.put("filename", normalize(request.getFilename()));
        value.put("sha256", normalizeLower(request.getSha256()));
        return hash(value);
    }

    public String hashTask(TaskCreateRequest request, String resolvedAgentCode) {
        Map<String, Object> value = new TreeMap<>();
        value.put("agentCode", normalize(resolvedAgentCode));
        value.put("callbackSecretHash", blank(request.getCallbackSecret()) ? null : CustomApiCrypto.sha256(request.getCallbackSecret()));
        value.put("callbackUrl", normalize(request.getCallbackUrl()));
        value.put("clientTaskId", normalize(request.getClientTaskId()));
        value.put("direction", blank(request.getDirection()) ? "import" : normalizeLower(request.getDirection()));
        value.put("fileId", normalize(request.getFileId()));
        value.put("metadata", request.getMetadata() == null ? Map.of() : new TreeMap<>(request.getMetadata()));
        value.put("responseMode", blank(request.getResponseMode()) ? "polling" : normalizeLower(request.getResponseMode()));
        return hash(value);
    }

    private String hash(Object value) {
        try {
            byte[] canonical = mapper.writeValueAsBytes(value);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(canonical));
        } catch (Exception e) {
            throw new JeecgBootException("canonical request hash failed: " + e.getMessage());
        }
    }

    private String normalize(String value) {
        return blank(value) ? null : value.trim();
    }

    private String normalizeLower(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toLowerCase();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
