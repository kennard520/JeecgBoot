package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.mapper.CustomApiFileMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.util.CustomApiIds;
import org.jeecg.modules.custom.api.vo.FileCompleteRequest;
import org.jeecg.modules.custom.api.vo.FileInfoResponse;
import org.jeecg.modules.custom.api.vo.FileUploadUrlRequest;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CustomApiFileServiceImpl extends ServiceImpl<CustomApiFileMapper, CustomApiFile> implements ICustomApiFileService {

    @Autowired
    private ObjectStorageService objectStorageService;

    @Value("${custom.api.upload-url-ttl-seconds:900}")
    private Long uploadUrlTtlSeconds;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadUrlResponse createUploadUrl(CustomApiApp app, FileUploadUrlRequest request, HttpServletRequest servletRequest) {
        if (request == null || isBlank(request.getFilename())) {
            throw new JeecgBootException("filename is required");
        }
        validateFileName(request.getFilename());

        String fileId = CustomApiIds.fileId();
        String safeFilename = CustomApiIds.safeFilename(request.getFilename());
        LocalDate today = LocalDate.now();
        String objectKey = String.format("custom-api/uploads/%04d/%02d/%02d/%s/%s",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(), fileId, safeFilename);
        String uploadToken = CustomApiCrypto.randomToken("upl_", 24);

        CustomApiFile file = new CustomApiFile()
                .setFileId(fileId)
                .setCustomerCode(app.getCustomerCode())
                .setClientFileId(request.getClientFileId())
                .setOriginalFilename(safeFilename)
                .setContentType(isBlank(request.getContentType()) ? "application/octet-stream" : request.getContentType())
                .setFileSize(request.getFileSize())
                .setSha256(request.getSha256())
                .setObjectKey(objectKey)
                .setUploadTokenHash(CustomApiCrypto.sha256(uploadToken))
                .setStatus(CustomApiFile.STATUS_PENDING)
                .setCreatedAt(LocalDateTime.now())
                .setExpiresAt(LocalDateTime.now().plusSeconds(uploadUrlTtlSeconds));

        FileUploadUrlResponse response = objectStorageService.createUploadUrl(file, uploadToken, servletRequest);
        save(file);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoResponse complete(CustomApiApp app, String fileId, FileCompleteRequest request) {
        CustomApiFile file = requireOwnedFile(app, fileId);
        if (CustomApiFile.STATUS_EXPIRED.equals(file.getStatus()) || CustomApiFile.STATUS_DELETED.equals(file.getStatus())) {
            throw new JeecgBootException("file cannot be completed in current status: " + file.getStatus());
        }
        if (file.getExpiresAt() != null && file.getExpiresAt().isBefore(LocalDateTime.now())) {
            file.setStatus(CustomApiFile.STATUS_EXPIRED);
            updateById(file);
            throw new JeecgBootException("upload url expired");
        }
        if (request != null) {
            if (request.getFileSize() != null) {
                file.setFileSize(request.getFileSize());
            }
            if (!isBlank(request.getSha256())) {
                file.setSha256(request.getSha256());
            }
        }
        file.setStatus(CustomApiFile.STATUS_UPLOADED);
        file.setUploadedAt(LocalDateTime.now());
        updateById(file);
        return toInfo(file);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadLocalContent(String fileId, String uploadToken, MultipartFile upload) {
        CustomApiFile file = getOne(new LambdaQueryWrapper<CustomApiFile>().eq(CustomApiFile::getFileId, fileId), false);
        if (file == null) {
            throw new JeecgBootException("file not found");
        }
        if (!CustomApiCrypto.equalsHash(uploadToken, file.getUploadTokenHash())) {
            throw new JeecgBootException("invalid upload token");
        }
        if (file.getExpiresAt() != null && file.getExpiresAt().isBefore(LocalDateTime.now())) {
            file.setStatus(CustomApiFile.STATUS_EXPIRED);
            updateById(file);
            throw new JeecgBootException("upload url expired");
        }
        objectStorageService.saveLocalUpload(file, upload);
        updateById(file);
    }

    @Override
    public CustomApiFile requireUploadedFile(CustomApiApp app, String fileId) {
        CustomApiFile file = requireOwnedFile(app, fileId);
        if (!CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            throw new JeecgBootException("file is not uploaded");
        }
        return file;
    }

    private CustomApiFile requireOwnedFile(CustomApiApp app, String fileId) {
        if (isBlank(fileId)) {
            throw new JeecgBootException("fileId is required");
        }
        CustomApiFile file = getOne(new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, fileId)
                .eq(CustomApiFile::getCustomerCode, app.getCustomerCode()), false);
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
