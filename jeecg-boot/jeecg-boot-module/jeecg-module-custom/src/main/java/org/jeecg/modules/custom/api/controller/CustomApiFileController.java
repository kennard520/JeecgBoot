package org.jeecg.modules.custom.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.CustomApiRateLimiter;
import org.jeecg.modules.custom.api.vo.FileCompleteRequest;
import org.jeecg.modules.custom.api.vo.FileInfoResponse;
import org.jeecg.modules.custom.api.vo.FileUploadUrlRequest;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Customs AI external files")
@RestController
@RequestMapping("/custom/api/files")
public class CustomApiFileController {

    @Autowired
    private ICustomApiAppService appService;
    @Autowired
    private ICustomApiFileService fileService;
    @Autowired
    private CustomApiRateLimiter rateLimiter;

    @IgnoreAuth
    @Operation(summary = "Create upload URL")
    @PostMapping("/upload-url")
    public Result<FileUploadUrlResponse> uploadUrl(
            @RequestBody FileUploadUrlRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            HttpServletRequest servletRequest) {
        CustomApiApp app = appService.requireApp(servletRequest);
        rateLimiter.check(app, "upload");
        return Result.OK(fileService.createUploadUrl(app, request, servletRequest, idempotencyKey));
    }

    @IgnoreAuth
    @Operation(summary = "Local upload fallback for development")
    @PostMapping("/{fileId}/content")
    public Result<?> uploadContent(@PathVariable String fileId,
                                   @RequestParam("uploadToken") String uploadToken,
                                   @RequestParam("file") MultipartFile file) {
        fileService.uploadLocalContent(fileId, uploadToken, file);
        return Result.OK("OK");
    }

    @IgnoreAuth
    @Operation(summary = "Complete upload")
    @PostMapping("/{fileId}/complete")
    public Result<FileInfoResponse> complete(@PathVariable String fileId,
                                             @RequestBody(required = false) FileCompleteRequest request,
                                             HttpServletRequest servletRequest) {
        CustomApiApp app = appService.requireApp(servletRequest);
        rateLimiter.check(app, "upload");
        return Result.OK(fileService.complete(app, fileId, request));
    }
}
