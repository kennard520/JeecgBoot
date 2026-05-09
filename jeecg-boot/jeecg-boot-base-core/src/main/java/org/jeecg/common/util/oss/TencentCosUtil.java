package org.jeecg.common.util.oss;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.filter.SsrfFileTypeFilter;
import org.jeecg.common.util.filter.StrAttackFilter;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 腾讯云 COS 上传工具类。
 */
@Slf4j
public class TencentCosUtil {

    private static String secretId;
    private static String secretKey;
    private static String region;
    private static String bucketName;
    private static String staticDomain;
    private static COSClient cosClient;

    public static void setSecretId(String secretId) {
        TencentCosUtil.secretId = secretId;
    }

    public static void setSecretKey(String secretKey) {
        TencentCosUtil.secretKey = secretKey;
    }

    public static void setRegion(String region) {
        TencentCosUtil.region = region;
    }

    public static void setBucketName(String bucketName) {
        TencentCosUtil.bucketName = bucketName;
    }

    public static void setStaticDomain(String staticDomain) {
        TencentCosUtil.staticDomain = staticDomain;
    }

    public static String upload(MultipartFile file, String fileDir) throws Exception {
        return upload(file, fileDir, null);
    }

    public static String upload(MultipartFile file, String fileDir, String customBucket) throws Exception {
        SsrfFileTypeFilter.checkUploadFileType(file, fileDir);

        String newBucket = oConvertUtils.isNotEmpty(customBucket) ? customBucket : bucketName;
        initCosClient();
        if (!cosClient.doesBucketExist(newBucket)) {
            cosClient.createBucket(newBucket);
        }

        String objectName = buildObjectName(fileDir, file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(oConvertUtils.isNotEmpty(file.getContentType()) ? file.getContentType() : "application/octet-stream");
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectResult result = cosClient.putObject(newBucket, objectName, inputStream, metadata);
            if (result != null) {
                log.info("------腾讯云COS文件上传成功------{}", objectName);
            }
        }
        return buildAccessUrl(newBucket, objectName);
    }

    public static String upload(InputStream stream, String relativePath) throws Exception {
        initCosClient();
        if (!cosClient.doesBucketExist(bucketName)) {
            cosClient.createBucket(bucketName);
        }
        String objectName = normalizeObjectName(relativePath);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(stream.available());
        metadata.setContentType("application/octet-stream");
        try {
            PutObjectResult result = cosClient.putObject(bucketName, objectName, stream, metadata);
            if (result != null) {
                log.info("------腾讯云COS文件上传成功------{}", objectName);
            }
        } finally {
            stream.close();
        }
        return buildAccessUrl(bucketName, objectName);
    }

    public static void deleteUrl(String url) {
        deleteUrl(url, null);
    }

    public static void deleteUrl(String url, String customBucket) {
        String newBucket = oConvertUtils.isNotEmpty(customBucket) ? customBucket : bucketName;
        initCosClient();
        cosClient.deleteObject(newBucket, replacePrefix(url, newBucket));
    }

    private static COSClient initCosClient() {
        if (cosClient == null) {
            COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            cosClient = new COSClient(credentials, clientConfig);
        }
        return cosClient;
    }

    private static String buildObjectName(String fileDir, MultipartFile file) {
        String safeFileDir = StrAttackFilter.filter(fileDir == null ? "" : fileDir);
        if (oConvertUtils.isNotEmpty(safeFileDir) && !safeFileDir.endsWith(SymbolConstant.SINGLE_SLASH)) {
            safeFileDir = safeFileDir.concat(SymbolConstant.SINGLE_SLASH);
        }

        String orgName = file.getOriginalFilename();
        if (oConvertUtils.isEmpty(orgName)) {
            orgName = file.getName();
        }
        orgName = CommonUtils.getFileName(orgName);
        String fileName = orgName.indexOf(SymbolConstant.SPOT) == -1
                ? orgName + "_" + System.currentTimeMillis()
                : orgName.substring(0, orgName.lastIndexOf(SymbolConstant.SPOT)) + "_" + System.currentTimeMillis()
                + orgName.substring(orgName.lastIndexOf(SymbolConstant.SPOT));
        return normalizeObjectName(safeFileDir + fileName);
    }

    private static String normalizeObjectName(String objectName) {
        String result = objectName == null ? "" : objectName;
        while (result.startsWith(SymbolConstant.SINGLE_SLASH)) {
            result = result.substring(1);
        }
        return result;
    }

    private static String buildAccessUrl(String bucket, String objectName) {
        if (oConvertUtils.isNotEmpty(staticDomain) && staticDomain.toLowerCase().startsWith(CommonConstant.STR_HTTP)) {
            return trimTrailingSlash(staticDomain) + SymbolConstant.SINGLE_SLASH + objectName;
        }
        return "https://" + bucket + ".cos." + region + ".myqcloud.com" + SymbolConstant.SINGLE_SLASH + objectName;
    }

    private static String replacePrefix(String objectName, String bucket) {
        String result = objectName == null ? "" : objectName;
        if (oConvertUtils.isNotEmpty(staticDomain)) {
            result = result.replace(trimTrailingSlash(staticDomain) + SymbolConstant.SINGLE_SLASH, "");
        } else {
            result = result.replace("https://" + bucket + ".cos." + region + ".myqcloud.com" + SymbolConstant.SINGLE_SLASH, "");
        }
        return normalizeObjectName(result);
    }

    private static String trimTrailingSlash(String value) {
        if (value == null) {
            return "";
        }
        String result = value;
        while (result.endsWith(SymbolConstant.SINGLE_SLASH)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
