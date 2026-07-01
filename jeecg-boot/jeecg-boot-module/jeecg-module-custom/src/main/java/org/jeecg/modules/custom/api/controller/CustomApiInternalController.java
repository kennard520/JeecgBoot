package org.jeecg.modules.custom.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Hidden;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Hidden
@RestController
@RequestMapping("/custom/api/internal")
public class CustomApiInternalController {

    @Autowired
    private ICustomApiFileService fileService;

    @Value("${custom.api.internal-token:}")
    private String internalToken;

    @IgnoreAuth
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<FileSystemResource> downloadFile(
            @PathVariable String fileId,
            @RequestHeader(value = "X-Custom-Api-Internal-Token", required = false) String token) {
        if (internalToken == null || internalToken.isBlank() || !internalToken.equals(token)) {
            throw new JeecgBootException("invalid internal token");
        }
        CustomApiFile file = fileService.getOne(new LambdaQueryWrapper<CustomApiFile>()
                .eq(CustomApiFile::getFileId, fileId), false);
        if (file == null || file.getStoragePath() == null || file.getStoragePath().isBlank()) {
            throw new JeecgBootException("file not found");
        }
        Path path = Path.of(file.getStoragePath());
        if (!Files.exists(path)) {
            throw new JeecgBootException("file content not found");
        }
        String filename = file.getOriginalFilename() == null ? fileId : file.getOriginalFilename();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(new FileSystemResource(path));
    }
}
