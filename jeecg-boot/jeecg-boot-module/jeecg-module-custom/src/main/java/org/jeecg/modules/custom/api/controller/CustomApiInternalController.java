package org.jeecg.modules.custom.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Hidden;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.security.InternalDownloadTokenService;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Hidden
@RestController
@RequestMapping("/custom/api/internal")
public class CustomApiInternalController {

    @Autowired
    private ICustomApiFileService fileService;

    @Autowired
    private CustomApiTaskMapper taskMapper;

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private InternalDownloadTokenService downloadTokenService;

    @IgnoreAuth
    @GetMapping("/tasks/{taskId}/files/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadTaskFile(
            @PathVariable String taskId,
            @PathVariable String fileId,
            @RequestParam int runNo,
            @RequestParam long expires,
            @RequestParam String signature) {
        downloadTokenService.validate(taskId, fileId, runNo, expires, signature);
        CustomApiTask task = taskMapper.selectOne(new LambdaQueryWrapper<CustomApiTask>()
                .eq(CustomApiTask::getTaskId, taskId));
        if (task == null || !Objects.equals(task.getFileId(), fileId)
                || !Objects.equals(task.getCustomsAiRunNo(), runNo)
                || !List.of(CustomApiTask.STATUS_QUEUED, CustomApiTask.STATUS_RUNNING).contains(task.getStatus())) {
            throw new JeecgBootException("task file is not available");
        }
        CustomApiFile file = fileService.getOne(new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, fileId), false);
        if (file == null || !CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())
                || !Objects.equals(task.getCustomerCode(), file.getCustomerCode())
                || task.getAppId() != null && file.getAppId() != null
                && !Objects.equals(task.getAppId(), file.getAppId())) {
            throw new JeecgBootException("task file is not available");
        }
        try {
            String filename = file.getOriginalFilename() == null ? fileId : file.getOriginalFilename();
            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            MediaType mediaType = parseMediaType(file.getContentType());
            ResponseEntity.BodyBuilder response = ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
            if (file.getActualFileSize() != null && file.getActualFileSize() > 0) {
                response.contentLength(file.getActualFileSize());
            }
            return response.body(new InputStreamResource(objectStorageService.openStream(file)));
        } catch (Exception e) {
            throw new JeecgBootException("open task file failed: " + e.getMessage());
        }
    }

    private MediaType parseMediaType(String contentType) {
        try {
            return contentType == null || contentType.isBlank()
                    ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType);
        } catch (Exception ignored) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
