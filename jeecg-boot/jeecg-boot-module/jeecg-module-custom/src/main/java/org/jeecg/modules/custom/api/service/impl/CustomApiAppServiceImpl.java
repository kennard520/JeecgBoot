package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.mapper.CustomApiAppMapper;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.jeecg.modules.custom.api.vo.AuthTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CustomApiAppServiceImpl extends ServiceImpl<CustomApiAppMapper, CustomApiApp> implements ICustomApiAppService {

    @Value("${custom.api.token-ttl-seconds:7200}")
    private Long tokenTtlSeconds;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthTokenResponse issueToken(AuthTokenRequest request) {
        if (request == null || isBlank(request.getAppKey()) || isBlank(request.getAppSecret())) {
            throw new JeecgBootException("appKey and appSecret are required");
        }
        CustomApiApp app = getOne(new LambdaQueryWrapper<CustomApiApp>().eq(CustomApiApp::getAppKey, request.getAppKey()), false);
        if (app == null || !app.isApiEnabled()) {
            throw new JeecgBootException("app is disabled or not found");
        }
        if (!CustomApiCrypto.equalsHash(request.getAppSecret(), app.getAppSecretHash())) {
            throw new JeecgBootException("invalid app secret");
        }

        String token = CustomApiCrypto.randomToken("cai_", 32);
        app.setAccessTokenHash(CustomApiCrypto.sha256(token));
        app.setTokenExpireAt(LocalDateTime.now().plusSeconds(tokenTtlSeconds));
        app.setUpdatedAt(LocalDateTime.now());
        updateById(app);

        return new AuthTokenResponse()
                .setAccessToken(token)
                .setTokenType("Bearer")
                .setExpiresIn(tokenTtlSeconds);
    }

    @Override
    public CustomApiApp requireApp(HttpServletRequest request) {
        String token = request.getHeader("X-Custom-Api-Token");
        if (isBlank(token)) {
            String auth = request.getHeader("Authorization");
            if (!isBlank(auth) && auth.startsWith("Bearer ")) {
                token = auth.substring("Bearer ".length()).trim();
            }
        }
        if (isBlank(token)) {
            throw new JeecgBootException("missing X-Custom-Api-Token");
        }
        CustomApiApp app = getOne(new LambdaQueryWrapper<CustomApiApp>()
                .eq(CustomApiApp::getAccessTokenHash, CustomApiCrypto.sha256(token)), false);
        if (app == null || !app.isApiEnabled()) {
            throw new JeecgBootException("invalid access token");
        }
        if (app.getTokenExpireAt() == null || app.getTokenExpireAt().isBefore(LocalDateTime.now())) {
            throw new JeecgBootException("access token expired");
        }
        return app;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
