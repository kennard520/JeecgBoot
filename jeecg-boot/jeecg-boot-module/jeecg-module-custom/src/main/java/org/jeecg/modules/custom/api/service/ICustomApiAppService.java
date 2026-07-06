package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.jeecg.modules.custom.api.vo.AuthTokenResponse;
import org.jeecg.modules.custom.api.vo.CustomApiAppResponse;
import org.jeecg.modules.custom.api.vo.CustomApiAppSaveRequest;
import org.jeecg.modules.custom.api.vo.CustomApiAppSecretResponse;

public interface ICustomApiAppService extends IService<CustomApiApp> {
    AuthTokenResponse issueToken(AuthTokenRequest request);

    CustomApiApp requireApp(HttpServletRequest request);

    CustomApiAppSecretResponse createApp(CustomApiAppSaveRequest request);

    CustomApiAppResponse updateApp(CustomApiAppSaveRequest request);

    CustomApiAppSecretResponse resetSecret(Long id);

    CustomApiAppResponse clearAccessToken(Long id);

    boolean appKeyExists(String appKey, Long excludeId);
}
