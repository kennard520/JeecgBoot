package org.jeecg.modules.custom.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.custom.task.entity.Document;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档解析任务 Service。
 */
public interface IDocumentService extends IService<Document> {

    /**
     * 保存用户上传的 zip 文件，并创建文档记录。
     */
    Document uploadZip(MultipartFile file);

    /**
     * 创建解析任务，记录任务 ID 和开始时间。
     */
    Document startParse(Long documentId);

    /**
     * 解析完成后绑定报关单 ID。
     */
    Document completeParse(String taskId, Long decHeadId);

    /**
     * 解析失败时记录失败原因。
     */
    Document failParse(String taskId, String errorMessage);
}
