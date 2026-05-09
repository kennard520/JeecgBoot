package org.jeecg.config.oss;

import jakarta.annotation.PostConstruct;
import org.jeecg.common.util.oss.TencentCosUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 腾讯云 COS 文件上传配置。
 */
@Lazy(false)
@Configuration
@ConditionalOnProperty(prefix = "jeecg.cos", name = "bucketName")
public class TencentCosConfiguration {

    @Value("${jeecg.cos.secretId}")
    private String secretId;

    @Value("${jeecg.cos.secretKey}")
    private String secretKey;

    @Value("${jeecg.cos.region}")
    private String region;

    @Value("${jeecg.cos.bucketName}")
    private String bucketName;

    @Value("${jeecg.cos.staticDomain:}")
    private String staticDomain;

    @PostConstruct
    public void initTencentCosConfiguration() {
        TencentCosUtil.setSecretId(secretId);
        TencentCosUtil.setSecretKey(secretKey);
        TencentCosUtil.setRegion(region);
        TencentCosUtil.setBucketName(bucketName);
        TencentCosUtil.setStaticDomain(staticDomain);
    }
}
