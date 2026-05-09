package org.jeecg.modules.custom.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.mapper.DocumentMapper;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.UUID;

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
    @Transactional(rollbackFor = Exception.class)
    public Document startParse(Long documentId) {
        Document document = requireDocument(documentId);
        if (Document.STATUS_PARSING.equals(document.getStatus())) {
            return document;
        }
        if (Document.STATUS_COMPLETED.equals(document.getStatus())) {
            throw new JeecgBootException("文档已解析完成，不能重复创建解析任务");
        }
        String taskId = UUID.randomUUID().toString().replace("-", "");
        document.markParseStarted(taskId);
        this.updateById(document);
        return document;
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
}
