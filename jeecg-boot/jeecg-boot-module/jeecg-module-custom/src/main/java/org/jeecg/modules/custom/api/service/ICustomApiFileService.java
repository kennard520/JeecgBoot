package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.vo.FileCompleteRequest;
import org.jeecg.modules.custom.api.vo.FileInfoResponse;
import org.jeecg.modules.custom.api.vo.FileUploadUrlRequest;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICustomApiFileService extends IService<CustomApiFile> {
    FileUploadUrlResponse createUploadUrl(CustomApiApp app, FileUploadUrlRequest request, HttpServletRequest servletRequest);

    FileUploadUrlResponse createUploadUrl(CustomApiApp app, FileUploadUrlRequest request,
                                          HttpServletRequest servletRequest, String idempotencyKey);

    FileInfoResponse complete(CustomApiApp app, String fileId, FileCompleteRequest request);

    void uploadLocalContent(String fileId, String uploadToken, MultipartFile file);

    CustomApiFile requireUploadedFile(CustomApiApp app, String fileId);
}
