package org.jeecg.modules.custom.api.storage;

import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.vo.FileDownloadInfo;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;

public interface ObjectStorageService {
    FileUploadUrlResponse createUploadUrl(CustomApiFile file, String uploadToken, HttpServletRequest request);

    FileDownloadInfo createDownloadUrl(CustomApiFile file, LocalDateTime expiresAt);

    void saveLocalUpload(CustomApiFile file, MultipartFile upload);

    InputStream openStream(CustomApiFile file) throws IOException;

    Path downloadToLocal(CustomApiFile file, Path workDir);
}
