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
import org.springframework.web.multipart.MultipartFile;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.LinkOption;
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
        if (isTencentCos() && hasTencentCosConfiguration()) {
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
        String uploadUrl = CommonUtils.getBaseUrl(request) + "/custom/api/files/" + file.getFileId() + "/content";
        FileUploadUrlResponse response = response(file, "POST", uploadUrl, expiresAt);
        response.getHeaders().put("X-Custom-Upload-Token", uploadToken);
        return response;
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
        Path temp = null;
        try {
            Path dir = Path.of(uploadPath, API_FILE_PATH, file.getFileId());
            Files.createDirectories(dir);
            Path target = dir.resolve(file.getOriginalFilename());
            temp = Files.createTempFile(dir, ".upload-", ".tmp");
            try (InputStream input = upload.getInputStream()) {
                Files.copy(input, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            moveReplacing(temp, target);
            temp = null;
            file.setStoragePath(target.toAbsolutePath().toString());
            if (upload.getContentType() != null) {
                file.setContentType(upload.getContentType());
            }
        } catch (Exception e) {
            throw new JeecgBootException("save local upload failed: " + e.getMessage());
        } finally {
            deleteQuietly(temp);
        }
    }

    @Override
    public void freezeUploadedObject(CustomApiFile file, String immutableObjectKey) {
        if (immutableObjectKey == null || immutableObjectKey.isBlank()) {
            throw new JeecgBootException("immutable object key is required");
        }
        if (immutableObjectKey.equals(file.getObjectKey())) {
            throw new JeecgBootException("immutable object key must differ from staging key");
        }
        try {
            if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(file.getStorageType())) {
                freezeLocalObject(file, immutableObjectKey);
            } else if (CommonConstant.UPLOAD_TYPE_OSS.equals(file.getStorageType())) {
                freezeOssObject(file, immutableObjectKey);
            } else if (isTencentCos(file.getStorageType())) {
                freezeCosObject(file, immutableObjectKey);
            } else {
                throw new JeecgBootException("unsupported storage type: " + file.getStorageType());
            }
            file.setObjectKey(immutableObjectKey);
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("freeze uploaded object failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteObject(CustomApiFile file) {
        if (file == null) {
            return;
        }
        try {
            if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(file.getStorageType())) {
                Path target = requireManagedLocalFile(file.getStoragePath(), false);
                Files.deleteIfExists(target);
                return;
            }
            if (CommonConstant.UPLOAD_TYPE_OSS.equals(file.getStorageType())) {
                OSSClient client = new OSSClient(ossEndpoint, new DefaultCredentialProvider(ossAccessKey, ossSecretKey), null);
                try {
                    client.deleteObject(file.getBucket(), file.getObjectKey());
                } finally {
                    client.shutdown();
                }
                return;
            }
            if (isTencentCos(file.getStorageType())) {
                COSClient client = createCosClient();
                try {
                    client.deleteObject(file.getBucket(), file.getObjectKey());
                } finally {
                    client.shutdown();
                }
            }
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("delete object failed: " + e.getMessage());
        }
    }

    @Override
    public InputStream openStream(CustomApiFile file) throws IOException {
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(file.getStorageType())) {
            if (file.getStoragePath() == null || file.getStoragePath().isBlank()) {
                throw new JeecgBootException("local upload path is missing");
            }
            Path source = requireManagedLocalFile(file.getStoragePath(), true);
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
                return requireManagedLocalFile(file.getStoragePath(), true);
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

    private void freezeLocalObject(CustomApiFile file, String immutableObjectKey) throws IOException {
        if (file.getStoragePath() == null || file.getStoragePath().isBlank()) {
            throw new JeecgBootException("local upload path is missing");
        }
        Path source = requireManagedLocalFile(file.getStoragePath(), true);
        Path target = resolveLocalObjectPath(immutableObjectKey);
        if (Files.exists(target)) {
            throw new JeecgBootException("immutable local object already exists");
        }
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), ".freeze-", ".tmp");
        try {
            Files.copy(source, temp, StandardCopyOption.REPLACE_EXISTING);
            moveWithoutReplace(temp, target);
            temp = null;
        } finally {
            deleteQuietly(temp);
        }
        file.setStoragePath(target.toString());
    }

    private void freezeOssObject(CustomApiFile file, String immutableObjectKey) {
        OSSClient client = new OSSClient(ossEndpoint, new DefaultCredentialProvider(ossAccessKey, ossSecretKey), null);
        try {
            if (client.doesObjectExist(file.getBucket(), immutableObjectKey)) {
                throw new JeecgBootException("immutable OSS object already exists");
            }
            client.copyObject(file.getBucket(), file.getObjectKey(), file.getBucket(), immutableObjectKey);
        } finally {
            client.shutdown();
        }
    }

    private void freezeCosObject(CustomApiFile file, String immutableObjectKey) {
        COSClient client = createCosClient();
        try {
            if (client.doesObjectExist(file.getBucket(), immutableObjectKey)) {
                throw new JeecgBootException("immutable COS object already exists");
            }
            client.copyObject(file.getBucket(), file.getObjectKey(), file.getBucket(), immutableObjectKey);
        } finally {
            client.shutdown();
        }
    }

    private Path resolveLocalObjectPath(String objectKey) {
        try {
            Path root = Path.of(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(root);
            Path target = root.resolve(objectKey).normalize();
            if (!target.startsWith(root)) {
                throw new JeecgBootException("immutable object key escapes upload root");
            }
            Files.createDirectories(target.getParent());
            if (!target.getParent().toRealPath().startsWith(root.toRealPath())) {
                throw new JeecgBootException("immutable object path escapes upload root");
            }
            return target;
        } catch (JeecgBootException e) {
            throw e;
        } catch (IOException e) {
            throw new JeecgBootException("resolve immutable object path failed: " + e.getMessage());
        }
    }

    private Path requireManagedLocalFile(String storagePath, boolean mustExist) throws IOException {
        if (storagePath == null || storagePath.isBlank()) {
            throw new JeecgBootException("local upload path is missing");
        }
        Path root = Path.of(uploadPath).toAbsolutePath().normalize();
        Files.createDirectories(root);
        Path source = Path.of(storagePath).toAbsolutePath().normalize();
        if (!source.startsWith(root) || Files.isSymbolicLink(source)) {
            throw new JeecgBootException("local upload path is outside the managed root or is a symbolic link");
        }
        if (mustExist && !Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
            throw new JeecgBootException("local upload file not found");
        }
        if (!mustExist && Files.exists(source, LinkOption.NOFOLLOW_LINKS)
                && !Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
            throw new JeecgBootException("local object is not a regular file");
        }
        Path parent = source.getParent();
        if (parent == null || (Files.exists(parent) && !parent.toRealPath().startsWith(root.toRealPath()))) {
            throw new JeecgBootException("local upload path escapes the managed root");
        }
        return source;
    }

    private void moveReplacing(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void moveWithoutReplace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source, target);
        }
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
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

    private boolean hasTencentCosConfiguration() {
        return hasText(cosSecretId) && hasText(cosSecretKey)
                && hasText(cosRegion) && hasText(cosBucketName);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String trimRightSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
