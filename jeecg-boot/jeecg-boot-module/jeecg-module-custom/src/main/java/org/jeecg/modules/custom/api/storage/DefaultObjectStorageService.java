package org.jeecg.modules.custom.api.storage;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.region.Region;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oss.OssBootUtil;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.vo.FileDownloadInfo;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DefaultObjectStorageService implements ObjectStorageService {
    private static final String API_FILE_PATH = "custom/api/files";

    @Value("${jeecg.uploadType:local}")
    private String uploadType;

    @Value("${jeecg.path.upload:}")
    private String uploadPath;

    @Value("${jeecg.oss.accessKey:}")
    private String ossAccessKey;
    @Value("${jeecg.oss.secretKey:}")
    private String ossSecretKey;
    @Value("${jeecg.oss.endpoint:}")
    private String ossEndpoint;
    @Value("${jeecg.oss.bucketName:}")
    private String ossBucketName;

    @Value("${jeecg.cos.secretId:}")
    private String cosSecretId;
    @Value("${jeecg.cos.secretKey:}")
    private String cosSecretKey;
    @Value("${jeecg.cos.region:}")
    private String cosRegion;
    @Value("${jeecg.cos.bucketName:}")
    private String cosBucketName;

    @Value("${custom.api.internal-base-url:http://localhost:8080}")
    private String internalBaseUrl;

    @Value("${custom.api.internal-token:}")
    private String internalToken;

    @Override
    public FileUploadUrlResponse createUploadUrl(CustomApiFile file, String uploadToken, HttpServletRequest request) {
        LocalDateTime expiresAt = file.getExpiresAt();
        if (isTencentCos()) {
            file.setStorageType(uploadType);
            file.setBucket(cosBucketName);
            return response(file, "PUT", createCosPutUrl(file, expiresAt), expiresAt);
        }
        if (CommonConstant.UPLOAD_TYPE_OSS.equals(uploadType)) {
            file.setStorageType(uploadType);
            file.setBucket(ossBucketName);
            return response(file, "PUT", createOssPutUrl(file, expiresAt), expiresAt);
        }
        file.setStorageType(CommonConstant.UPLOAD_TYPE_LOCAL);
        file.setBucket(null);
        String uploadUrl = CommonUtils.getBaseUrl(request) + "/custom/api/files/" + file.getFileId()
                + "/content?uploadToken=" + uploadToken;
        return response(file, "POST", uploadUrl, expiresAt);
    }

    @Override
    public FileDownloadInfo createDownloadUrl(CustomApiFile file, LocalDateTime expiresAt) {
        if (CommonConstant.UPLOAD_TYPE_OSS.equals(file.getStorageType())) {
            return new FileDownloadInfo().setUrl(createOssGetUrl(file, expiresAt));
        }
        if (isTencentCos(file.getStorageType())) {
            return new FileDownloadInfo().setUrl(createCosGetUrl(file, expiresAt));
        }
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(file.getStorageType())) {
            if (internalToken == null || internalToken.isBlank()) {
                throw new JeecgBootException("custom.api.internal-token is required for local download");
            }
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("X-Custom-Api-Internal-Token", internalToken);
            return new FileDownloadInfo()
                    .setUrl(trimRightSlash(internalBaseUrl) + "/custom/api/internal/files/" + file.getFileId() + "/download")
                    .setHeaders(headers);
        }
        throw new JeecgBootException("unsupported storage type: " + file.getStorageType());
    }

    @Override
    public void saveLocalUpload(CustomApiFile file, MultipartFile upload) {
        if (upload == null || upload.isEmpty()) {
            throw new JeecgBootException("upload file is empty");
        }
        try {
            Path dir = Path.of(uploadPath, API_FILE_PATH, file.getFileId());
            Files.createDirectories(dir);
            Path target = dir.resolve(file.getOriginalFilename());
            FileCopyUtils.copy(upload.getBytes(), target.toFile());
            file.setStoragePath(target.toAbsolutePath().toString());
            if (upload.getContentType() != null) {
                file.setContentType(upload.getContentType());
            }
        } catch (Exception e) {
            throw new JeecgBootException("save local upload failed: " + e.getMessage());
        }
    }

    @Override
    public InputStream openStream(CustomApiFile file) throws IOException {
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(file.getStorageType())) {
            if (file.getStoragePath() == null || file.getStoragePath().isBlank()) {
                throw new JeecgBootException("local upload path is missing");
            }
            Path source = Path.of(file.getStoragePath());
            if (!Files.isRegularFile(source)) {
                throw new JeecgBootException("local upload file not found");
            }
            return Files.newInputStream(source);
        }
        if (CommonConstant.UPLOAD_TYPE_OSS.equals(file.getStorageType())) {
            InputStream input = OssBootUtil.getOssFile(file.getObjectKey(), file.getBucket());
            if (input == null) {
                throw new JeecgBootException("OSS object not found");
            }
            return input;
        }
        if (isTencentCos(file.getStorageType())) {
            COSClient client = createCosClient();
            try {
                COSObject object = client.getObject(file.getBucket(), file.getObjectKey());
                return new FilterInputStream(object.getObjectContent()) {
                    @Override
                    public void close() throws IOException {
                        try {
                            super.close();
                        } finally {
                            client.shutdown();
                        }
                    }
                };
            } catch (RuntimeException e) {
                client.shutdown();
                throw e;
            }
        }
        throw new JeecgBootException("unsupported storage type: " + file.getStorageType());
    }

    @Override
    public Path downloadToLocal(CustomApiFile file, Path workDir) {
        try {
            Files.createDirectories(workDir);
            Path target = workDir.resolve(file.getOriginalFilename());
            if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(file.getStorageType())) {
                Path source = Path.of(file.getStoragePath());
                if (!Files.exists(source)) {
                    throw new JeecgBootException("local upload file not found");
                }
                return source;
            }
            if (CommonConstant.UPLOAD_TYPE_OSS.equals(file.getStorageType())) {
                try (InputStream in = OssBootUtil.getOssFile(file.getObjectKey(), file.getBucket())) {
                    if (in == null) {
                        throw new JeecgBootException("OSS object not found");
                    }
                    Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                return target;
            }
            if (isTencentCos(file.getStorageType())) {
                COSClient client = createCosClient();
                try {
                    COSObject object = client.getObject(file.getBucket(), file.getObjectKey());
                    try (InputStream in = object.getObjectContent()) {
                        Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                } finally {
                    client.shutdown();
                }
                return target;
            }
            throw new JeecgBootException("unsupported storage type: " + file.getStorageType());
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("download upload file failed: " + e.getMessage());
        }
    }

    private FileUploadUrlResponse response(CustomApiFile file, String method, String url, LocalDateTime expiresAt) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", file.getContentType());
        return new FileUploadUrlResponse()
                .setFileId(file.getFileId())
                .setStorageType(file.getStorageType())
                .setObjectKey(file.getObjectKey())
                .setUploadMethod(method)
                .setUploadUrl(url)
                .setHeaders(headers)
                .setExpiresAt(expiresAt);
    }

    private String createOssPutUrl(CustomApiFile file, LocalDateTime expiresAt) {
        OSSClient client = new OSSClient(ossEndpoint, new DefaultCredentialProvider(ossAccessKey, ossSecretKey), null);
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(file.getBucket(), file.getObjectKey(), HttpMethod.PUT);
            request.setExpiration(toDate(expiresAt));
            request.setContentType(file.getContentType());
            URL url = client.generatePresignedUrl(request);
            return url.toString();
        } finally {
            client.shutdown();
        }
    }

    private String createOssGetUrl(CustomApiFile file, LocalDateTime expiresAt) {
        OSSClient client = new OSSClient(ossEndpoint, new DefaultCredentialProvider(ossAccessKey, ossSecretKey), null);
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(file.getBucket(), file.getObjectKey(), HttpMethod.GET);
            request.setExpiration(toDate(expiresAt));
            URL url = client.generatePresignedUrl(request);
            return url.toString();
        } finally {
            client.shutdown();
        }
    }

    private String createCosPutUrl(CustomApiFile file, LocalDateTime expiresAt) {
        COSClient client = createCosClient();
        try {
            com.qcloud.cos.model.GeneratePresignedUrlRequest request =
                    new com.qcloud.cos.model.GeneratePresignedUrlRequest(file.getBucket(), file.getObjectKey(), HttpMethodName.PUT);
            request.setExpiration(toDate(expiresAt));
            URL url = client.generatePresignedUrl(request);
            return url.toString();
        } finally {
            client.shutdown();
        }
    }

    private String createCosGetUrl(CustomApiFile file, LocalDateTime expiresAt) {
        COSClient client = createCosClient();
        try {
            com.qcloud.cos.model.GeneratePresignedUrlRequest request =
                    new com.qcloud.cos.model.GeneratePresignedUrlRequest(file.getBucket(), file.getObjectKey(), HttpMethodName.GET);
            request.setExpiration(toDate(expiresAt));
            URL url = client.generatePresignedUrl(request);
            return url.toString();
        } finally {
            client.shutdown();
        }
    }

    private COSClient createCosClient() {
        COSCredentials credentials = new BasicCOSCredentials(cosSecretId, cosSecretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(cosRegion));
        return new COSClient(credentials, clientConfig);
    }

    private Date toDate(LocalDateTime value) {
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    private boolean isTencentCos() {
        return isTencentCos(uploadType);
    }

    private boolean isTencentCos(String value) {
        return CommonConstant.UPLOAD_TYPE_TENCENT_COS.equals(value) || CommonConstant.UPLOAD_TYPE_COS.equals(value);
    }

    private String trimRightSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
