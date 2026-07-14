package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.mapper.CustomApiFileMapper;
import org.jeecg.modules.custom.api.service.CustomApiIdempotencyService;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.jeecg.modules.custom.api.util.CanonicalRequestHasher;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.util.CustomApiIds;
import org.jeecg.modules.custom.api.validation.UploadedFileVerifier;
import org.jeecg.modules.custom.api.validation.VerifiedFile;
import org.jeecg.modules.custom.api.vo.FileCompleteRequest;
import org.jeecg.modules.custom.api.vo.FileInfoResponse;
import org.jeecg.modules.custom.api.vo.FileUploadUrlRequest;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomApiFileServiceImpl extends ServiceImpl<CustomApiFileMapper, CustomApiFile> implements ICustomApiFileService {

    private final ObjectStorageService objectStorageService;
    private final UploadedFileVerifier uploadedFileVerifier;
    private final CustomApiIdempotencyService idempotencyService;
    private final CanonicalRequestHasher requestHasher;

    @Value("${custom.api.upload-url-ttl-seconds:900}")
    private Long uploadUrlTtlSeconds = 900L;

    @Value("${custom.api.file.max-upload-bytes:104857600}")
    private Long maxUploadBytes = 104857600L;

    @Value("${custom.api.upload-capability-secret:${custom.api.internal-token:}}")
    private String uploadCapabilitySecret;

    public CustomApiFileServiceImpl(ObjectStorageService objectStorageService,
                                    UploadedFileVerifier uploadedFileVerifier,
                                    CustomApiIdempotencyService idempotencyService,
                                    CanonicalRequestHasher requestHasher) {
        this.objectStorageService = objectStorageService;
        this.uploadedFileVerifier = uploadedFileVerifier;
        this.idempotencyService = idempotencyService;
        this.requestHasher = requestHasher;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadUrlResponse createUploadUrl(CustomApiApp app, FileUploadUrlRequest request, HttpServletRequest servletRequest) {
        String idempotencyKey = request == null ? null : request.getIdempotencyKey();
        return createUploadUrl(app, request, servletRequest, idempotencyKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadUrlResponse createUploadUrl(CustomApiApp app, FileUploadUrlRequest request,
                                                 HttpServletRequest servletRequest, String headerIdempotencyKey) {
        if (request == null || isBlank(request.getFilename())) {
            throw new JeecgBootException("filename is required");
        }
        if (app == null || app.getId() == null) {
            throw new JeecgBootException("authenticated API app is required");
        }
        validateFileName(request.getFilename());
        if (request.getFileSize() != null && (request.getFileSize() <= 0 || request.getFileSize() > maxUploadBytes)) {
            throw new JeecgBootException("fileSize must be between 1 and " + maxUploadBytes);
        }
        if (!isBlank(request.getSha256()) && !request.getSha256().matches("(?i)^[0-9a-f]{64}$")) {
            throw new JeecgBootException("sha256 must be 64 hexadecimal characters");
        }

        String idempotencyKey = firstNonBlank(headerIdempotencyKey, request.getIdempotencyKey());
        String requestHash = requestHasher.hashFile(request);
        CustomApiFile existing = idempotencyService.findFile(
                app.getId(), request.getClientFileId(), idempotencyKey, requestHash);
        if (existing != null) {
            return existingUploadResponse(existing, servletRequest);
        }

        String fileId = CustomApiIds.fileId();
        String safeFilename = CustomApiIds.safeFilename(request.getFilename());
        LocalDate today = LocalDate.now();
        String objectKey = String.format("custom-api/uploads/%04d/%02d/%02d/%s/%s",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(), fileId, safeFilename);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(uploadUrlTtlSeconds).withNano(0);

        CustomApiFile file = new CustomApiFile()
                .setAppId(app.getId())
                .setFileId(fileId)
                .setCustomerCode(app.getCustomerCode())
                .setClientFileId(request.getClientFileId())
                .setIdempotencyKey(trimToNull(idempotencyKey))
                .setRequestHash(requestHash)
                .setOriginalFilename(safeFilename)
                .setContentType(isBlank(request.getContentType()) ? "application/octet-stream" : request.getContentType())
                .setFileSize(request.getFileSize())
                .setSha256(request.getSha256())
                .setObjectKey(objectKey)
                .setStatus(CustomApiFile.STATUS_PENDING)
                .setCreatedAt(LocalDateTime.now())
                .setExpiresAt(expiresAt);
        String uploadToken = stableUploadToken(file);
        file.setUploadTokenHash(CustomApiCrypto.sha256(uploadToken));

        FileUploadUrlResponse response = objectStorageService.createUploadUrl(file, uploadToken, servletRequest);
        CustomApiFile persisted = idempotencyService.insertFileOrFindWinner(file);
        return persisted == file ? response : existingUploadResponse(persisted, servletRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoResponse complete(CustomApiApp app, String fileId, FileCompleteRequest request) {
        CustomApiFile file = requireOwnedFile(app, fileId, true);
        if (!CustomApiFile.STATUS_PENDING.equals(file.getStatus())) {
            throw new JeecgBootException("file cannot be completed in current status: " + file.getStatus());
        }
        if (file.getExpiresAt() != null && file.getExpiresAt().isBefore(LocalDateTime.now())) {
            file.setStatus(CustomApiFile.STATUS_EXPIRED);
            updateById(file);
            throw new JeecgBootException("upload url expired");
        }
        if (request != null) {
            // Client completion metadata is intentionally ignored. The object is verified below.
        }
        String immutableObjectKey = immutableObjectKey(file);
        objectStorageService.freezeUploadedObject(file, immutableObjectKey);
        file.setObjectKey(immutableObjectKey);
        VerifiedFile verified = uploadedFileVerifier.verify(file);
        file.setFileSize(verified.actualFileSize());
        file.setSha256(verified.actualSha256());
        file.setActualFileSize(verified.actualFileSize());
        file.setActualSha256(verified.actualSha256());
        file.setVerifiedAt(LocalDateTime.now());
        file.setContentType(verified.detectedType());
        file.setUploadTokenHash(null);
        file.setStatus(CustomApiFile.STATUS_UPLOADED);
        file.setUploadedAt(LocalDateTime.now());
        updateById(file);
        return toInfo(file);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadLocalContent(String fileId, String uploadToken, MultipartFile upload) {
        CustomApiFile file = getOne(new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, fileId)
                .last("FOR UPDATE"), false);
        if (file == null) {
            throw new JeecgBootException("file not found");
        }
        if (!CustomApiFile.STATUS_PENDING.equals(file.getStatus())) {
            throw new JeecgBootException("file content cannot be uploaded in current status: " + file.getStatus());
        }
        if (file.getExpiresAt() != null && file.getExpiresAt().isBefore(LocalDateTime.now())) {
            file.setStatus(CustomApiFile.STATUS_EXPIRED);
            updateById(file);
            throw new JeecgBootException("upload url expired");
        }
        if (!CustomApiCrypto.equalsHash(uploadToken, file.getUploadTokenHash())) {
            throw new JeecgBootException("invalid upload token");
        }
        objectStorageService.saveLocalUpload(file, upload);
        file.setUploadTokenHash(null);
        updateById(file);
    }

    @Override
    public CustomApiFile requireUploadedFile(CustomApiApp app, String fileId) {
        CustomApiFile file = requireOwnedFile(app, fileId, false);
        if (!CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            throw new JeecgBootException("file is not uploaded");
        }
        return file;
    }

    private CustomApiFile requireOwnedFile(CustomApiApp app, String fileId, boolean forUpdate) {
        if (isBlank(fileId)) {
            throw new JeecgBootException("fileId is required");
        }
        LambdaQueryWrapper<CustomApiFile> query = new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, fileId)
                .eq(CustomApiFile::getAppId, app.getId())
                .eq(CustomApiFile::getCustomerCode, app.getCustomerCode());
        if (forUpdate) {
            query.last("FOR UPDATE");
        }
        CustomApiFile file = getOne(query, false);
        if (file == null) {
            throw new JeecgBootException("file not found");
        }
        return file;
    }

    private FileInfoResponse toInfo(CustomApiFile file) {
        return new FileInfoResponse()
                .setFileId(file.getFileId())
                .setStatus(file.getStatus())
                .setFilename(file.getOriginalFilename())
                .setFileSize(file.getFileSize());
    }

    private void validateFileName(String filename) {
        String lower = filename.toLowerCase();
        if (!(lower.endsWith(".zip") || lower.endsWith(".rar") || lower.endsWith(".pdf"))) {
            throw new JeecgBootException("only .zip, .rar, .pdf are supported");
        }
    }

    private FileUploadUrlResponse existingUploadResponse(CustomApiFile file, HttpServletRequest request) {
        if (CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            return new FileUploadUrlResponse()
                    .setFileId(file.getFileId())
                    .setStorageType(file.getStorageType())
                    .setObjectKey(file.getObjectKey())
                    .setExpiresAt(file.getExpiresAt());
        }
        if (!CustomApiFile.STATUS_PENDING.equals(file.getStatus())) {
            throw new JeecgBootException("file upload capability is unavailable in current status: " + file.getStatus());
        }
        if (file.getExpiresAt() == null || file.getExpiresAt().isBefore(LocalDateTime.now())) {
            file.setStatus(CustomApiFile.STATUS_EXPIRED);
            updateById(file);
            throw new JeecgBootException("upload capability expired");
        }
        if (isBlank(file.getUploadTokenHash())) {
            throw new JeecgBootException("upload capability was already consumed; complete the existing file");
        }
        String uploadToken = stableUploadToken(file);
        if (!CustomApiCrypto.equalsHash(uploadToken, file.getUploadTokenHash())) {
            throw new JeecgBootException("existing upload capability cannot be replayed safely");
        }
        return objectStorageService.createUploadUrl(file, uploadToken, request);
    }

    private String stableUploadToken(CustomApiFile file) {
        if (isBlank(uploadCapabilitySecret)) {
            throw new JeecgBootException("custom.api.upload-capability-secret is required");
        }
        if (file.getExpiresAt() == null) {
            throw new JeecgBootException("upload capability expiry is required");
        }
        String material = file.getFileId() + "\n"
                + (file.getRequestHash() == null ? "" : file.getRequestHash()) + "\n"
                + file.getExpiresAt().withNano(0);
        String signature = CustomApiCrypto.hmacSha256(uploadCapabilitySecret,
                material.getBytes(StandardCharsets.UTF_8));
        return "upl_" + CustomApiCrypto.sha256(signature);
    }

    private String immutableObjectKey(CustomApiFile file) {
        String version = UUID.randomUUID().toString().replace("-", "");
        String filename = isBlank(file.getOriginalFilename()) ? "upload.bin" : file.getOriginalFilename();
        return "custom-api/objects/" + file.getFileId() + "/" + version + "/" + filename;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
