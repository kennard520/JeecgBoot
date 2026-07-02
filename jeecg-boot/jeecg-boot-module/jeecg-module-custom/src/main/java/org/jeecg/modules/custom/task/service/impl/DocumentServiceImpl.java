package org.jeecg.modules.custom.task.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mq.CustomApiTaskMqProducer;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.util.CustomApiIds;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.mapper.DocumentMapper;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 文档解析任务 Service 实现。
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements IDocumentService {

    private static final String DOCUMENT_BIZ_PATH = "custom/task/document";

    @Value("${jeecg.path.upload}")
    private String uploadPath;

    @Value("${jeecg.uploadType}")
    private String uploadType;

    @Value("${jeecg.oss.bucketName:}")
    private String ossBucketName;

    @Value("${jeecg.cos.bucketName:}")
    private String cosBucketName;

    @Value("${custom.api.internal-customer-code:INTERNAL}")
    private String internalCustomerCode;

    @Value("${custom.api.internal-company-code:CUSTOMS}")
    private String internalCompanyCode;

    @Autowired
    private ICustomApiFileService customApiFileService;

    @Autowired
    private CustomApiTaskMapper customApiTaskMapper;

    @Autowired
    private CustomApiTaskMqProducer customApiTaskMqProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document uploadZip(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new JeecgBootException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (oConvertUtils.isEmpty(originalFilename) || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new JeecgBootException("仅支持上传.zip压缩包");
        }

        String storagePath = CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)
                ? CommonUtils.uploadLocal(file, DOCUMENT_BIZ_PATH, uploadPath)
                : CommonUtils.upload(file, DOCUMENT_BIZ_PATH, uploadType);
        if (oConvertUtils.isEmpty(storagePath)) {
            throw new JeecgBootException("文件上传失败");
        }

        Document document = new Document();
        document.markUploaded(originalFilename, storagePath, uploadType, file.getSize(), file.getContentType());
        this.save(document);
        return document;
    }

    @Override
    public Document startParse(Long documentId) {
        Document document = requireDocument(documentId);
        if (Document.STATUS_PARSING.equals(document.getStatus())) {
            return document;
        }
        if (Document.STATUS_COMPLETED.equals(document.getStatus())) {
            throw new JeecgBootException("文档已解析完成，不能重复创建解析任务");
        }
        String taskId = CustomApiIds.taskId();
        CustomApiFile file = buildApiFile(document);
        CustomApiTask task = buildApiTask(document, file, taskId);

        customApiFileService.save(file);
        document.markParseStarted(taskId);
        this.updateById(document);
        customApiTaskMapper.insert(task);

        try {
            customApiTaskMqProducer.sendParseTask(task, file);
        } catch (Exception e) {
            task.setStatus(CustomApiTask.STATUS_FAILED);
            task.setStage("enqueue_failed");
            task.setErrorCode(truncate(e.getClass().getSimpleName(), 100));
            task.setErrorMessage(truncate(e.getMessage(), 1000));
            task.setFinishedAt(LocalDateTime.now());
            customApiTaskMapper.updateById(task);
            document.markFailed(e.getMessage());
            this.updateById(document);
            throw new JeecgBootException("failed to enqueue parse task: " + e.getMessage());
        }
        return document;
    }

    private CustomApiFile buildApiFile(Document document) {
        String storageType = firstNonBlank(document.getStorageType(), uploadType, CommonConstant.UPLOAD_TYPE_LOCAL);
        String originalFilename = firstNonBlank(document.getOriginalFilename(), document.getFilename(), "upload.zip");
        String storagePath = resolveStoragePath(document, storageType);
        LocalDateTime now = LocalDateTime.now();
        return new CustomApiFile()
                .setFileId(CustomApiIds.fileId())
                .setCustomerCode(firstNonBlank(internalCustomerCode, "INTERNAL"))
                .setClientFileId(document.getId() == null ? null : "document_" + document.getId())
                .setOriginalFilename(CustomApiIds.safeFilename(originalFilename))
                .setContentType(firstNonBlank(document.getContentType(), "application/zip"))
                .setFileSize(document.getFileSize())
                .setStorageType(storageType)
                .setBucket(resolveBucket(storageType))
                .setObjectKey(resolveObjectKey(storagePath, storageType))
                .setStoragePath(storagePath)
                .setStatus(CustomApiFile.STATUS_UPLOADED)
                .setCreatedAt(now)
                .setUploadedAt(firstNonNull(document.getUploadedAt(), now));
    }

    private CustomApiTask buildApiTask(Document document, CustomApiFile file, String taskId) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("source", "task-page");
        metadata.put("documentId", document.getId());
        return new CustomApiTask()
                .setTaskId(taskId)
                .setFileId(file.getFileId())
                .setCustomerCode(file.getCustomerCode())
                .setClientTaskId(document.getId() == null ? null : "document_" + document.getId())
                .setDocumentId(document.getId())
                .setDirection("import")
                .setCompanyCode(firstNonBlank(internalCompanyCode, "CUSTOMS"))
                .setResponseMode("polling")
                .setStatus(CustomApiTask.STATUS_QUEUED)
                .setStage("queued")
                .setProgress(0)
                .setMetadataJson(JSON.toJSONString(metadata))
                .setCreatedAt(LocalDateTime.now());
    }

    private String resolveStoragePath(Document document, String storageType) {
        String storagePath = document.getStoragePath();
        if (!CommonConstant.UPLOAD_TYPE_LOCAL.equals(storageType) || oConvertUtils.isEmpty(storagePath)) {
            return storagePath;
        }
        Path path = Path.of(storagePath);
        if (path.isAbsolute()) {
            return path.toString();
        }
        return Path.of(uploadPath, storagePath).toAbsolutePath().normalize().toString();
    }

    private String resolveObjectKey(String storagePath, String storageType) {
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(storageType) || oConvertUtils.isEmpty(storagePath)) {
            return storagePath;
        }
        if (storagePath.startsWith("http://") || storagePath.startsWith("https://")) {
            try {
                String path = URI.create(storagePath).getPath();
                while (path != null && path.startsWith("/")) {
                    path = path.substring(1);
                }
                return path;
            } catch (Exception ignored) {
                return storagePath;
            }
        }
        return storagePath;
    }

    private String resolveBucket(String storageType) {
        if (CommonConstant.UPLOAD_TYPE_OSS.equals(storageType)) {
            return ossBucketName;
        }
        if (CommonConstant.UPLOAD_TYPE_TENCENT_COS.equals(storageType) || CommonConstant.UPLOAD_TYPE_COS.equals(storageType)) {
            return cosBucketName;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document completeParse(String taskId, Long decHeadId) {
        if (oConvertUtils.isEmpty(taskId)) {
            throw new JeecgBootException("解析任务ID不能为空");
        }
        if (decHeadId == null) {
            throw new JeecgBootException("报关单ID不能为空");
        }
        Document document = requireDocumentByTaskId(taskId);
        document.markParsed(decHeadId);
        this.updateById(document);
        return document;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document failParse(String taskId, String errorMessage) {
        if (oConvertUtils.isEmpty(taskId)) {
            throw new JeecgBootException("解析任务ID不能为空");
        }
        Document document = requireDocumentByTaskId(taskId);
        document.markFailed(errorMessage);
        this.updateById(document);
        return document;
    }

    private Document requireDocument(Long documentId) {
        if (documentId == null) {
            throw new JeecgBootException("文档ID不能为空");
        }
        Document document = this.getById(documentId);
        if (document == null) {
            throw new JeecgBootException("文档不存在");
        }
        return document;
    }

    private Document requireDocumentByTaskId(String taskId) {
        Document document = this.getOne(new LambdaQueryWrapper<Document>().eq(Document::getTaskId, taskId), false);
        if (document == null) {
            throw new JeecgBootException("解析任务不存在");
        }
        return document;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (oConvertUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    @SafeVarargs
    private final <T> T firstNonNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
