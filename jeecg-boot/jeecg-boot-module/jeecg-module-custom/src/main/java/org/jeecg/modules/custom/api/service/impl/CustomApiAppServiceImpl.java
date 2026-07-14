package org.jeecg.modules.custom.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.ai.vo.ApiAppAgentGrantRequest;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.exception.CustomApiUnauthorizedException;
import org.jeecg.modules.custom.api.mapper.CustomApiAppMapper;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.jeecg.modules.custom.api.service.CustomApiRateLimiter;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.jeecg.modules.custom.api.vo.AuthTokenResponse;
import org.jeecg.modules.custom.api.vo.CustomApiAppResponse;
import org.jeecg.modules.custom.api.vo.CustomApiAppSaveRequest;
import org.jeecg.modules.custom.api.vo.CustomApiAppSecretResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomApiAppServiceImpl extends ServiceImpl<CustomApiAppMapper, CustomApiApp> implements ICustomApiAppService {

    @Value("${custom.api.token-ttl-seconds:7200}")
    private Long tokenTtlSeconds;

    @Autowired
    private CustomApiRateLimiter rateLimiter;

    @Autowired
    private CustomAgentAccessService agentAccessService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomApiAppSecretResponse createApp(CustomApiAppSaveRequest request) {
        validateSaveRequest(request, true);
        if (appKeyExists(request.getAppKey(), null)) {
            throw new JeecgBootException("appKey already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        String appSecret = CustomApiCrypto.randomToken("cai_sec_", 24);
        CustomApiApp app = new CustomApiApp()
                .setAppKey(request.getAppKey().trim())
                .setAppSecretHash(CustomApiCrypto.sha256(appSecret))
                .setCustomerCode(request.getCustomerCode().trim())
                .setCompanyCode(resolveDefaultAgentCode(request))
                .setEnabled(request.getEnabled() == null ? 1 : request.getEnabled())
                .setRateLimit(request.getRateLimit() == null ? 60 : request.getRateLimit())
                .setCreatedAt(now)
                .setUpdatedAt(now);
        save(app);
        agentAccessService.replaceApiAppAgents(toAgentGrantRequest(app.getId(), request));
        return CustomApiAppSecretResponse.fromApp(CustomApiAppResponse.fromEntity(app), appSecret);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomApiAppResponse updateApp(CustomApiAppSaveRequest request) {
        validateSaveRequest(request, false);
        CustomApiApp app = getById(request.getId());
        if (app == null) {
            throw new JeecgBootException("app not found");
        }
        if (appKeyExists(request.getAppKey(), request.getId())) {
            throw new JeecgBootException("appKey already exists");
        }

        app.setAppKey(request.getAppKey().trim())
                .setCustomerCode(request.getCustomerCode().trim())
                .setCompanyCode(resolveDefaultAgentCode(request))
                .setEnabled(request.getEnabled() == null ? 1 : request.getEnabled())
                .setRateLimit(request.getRateLimit() == null ? 60 : request.getRateLimit())
                .setUpdatedAt(LocalDateTime.now());
        updateById(app);
        agentAccessService.replaceApiAppAgents(toAgentGrantRequest(app.getId(), request));
        return CustomApiAppResponse.fromEntity(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomApiAppSecretResponse resetSecret(Long id) {
        CustomApiApp app = getById(id);
        if (app == null) {
            throw new JeecgBootException("app not found");
        }
        String appSecret = CustomApiCrypto.randomToken("cai_sec_", 24);
        app.setAppSecretHash(CustomApiCrypto.sha256(appSecret));
        app.setAccessTokenHash(null);
        app.setTokenExpireAt(null);
        app.setUpdatedAt(LocalDateTime.now());
        updateById(app);
        return CustomApiAppSecretResponse.fromApp(CustomApiAppResponse.fromEntity(app), appSecret);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomApiAppResponse clearAccessToken(Long id) {
        CustomApiApp app = getById(id);
        if (app == null) {
            throw new JeecgBootException("app not found");
        }
        app.setAccessTokenHash(null);
        app.setTokenExpireAt(null);
        app.setUpdatedAt(LocalDateTime.now());
        updateById(app);
        return CustomApiAppResponse.fromEntity(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApp(Long id) {
        if (id == null) {
            return;
        }
        agentAccessService.deleteApiAppAgents(id);
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApps(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        ids.forEach(agentAccessService::deleteApiAppAgents);
        removeByIds(ids);
    }

    @Override
    public boolean appKeyExists(String appKey, Long excludeId) {
        if (isBlank(appKey)) {
            return false;
        }
        LambdaQueryWrapper<CustomApiApp> wrapper = new LambdaQueryWrapper<CustomApiApp>()
                .eq(CustomApiApp::getAppKey, appKey.trim());
        if (excludeId != null) {
            wrapper.ne(CustomApiApp::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthTokenResponse issueToken(AuthTokenRequest request) {
        if (request == null || isBlank(request.getAppKey()) || isBlank(request.getAppSecret())) {
            throw new JeecgBootException("appKey and appSecret are required");
        }
        CustomApiApp app = getOne(new LambdaQueryWrapper<CustomApiApp>().eq(CustomApiApp::getAppKey, request.getAppKey()), false);
        if (app == null || !app.isApiEnabled()) {
            throw new CustomApiUnauthorizedException("app is disabled or not found");
        }
        rateLimiter.check(app, "token");
        if (!CustomApiCrypto.equalsHash(request.getAppSecret(), app.getAppSecretHash())) {
            throw new CustomApiUnauthorizedException("invalid app secret");
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
            throw new CustomApiUnauthorizedException("missing X-Custom-Api-Token");
        }
        CustomApiApp app = getOne(new LambdaQueryWrapper<CustomApiApp>()
                .eq(CustomApiApp::getAccessTokenHash, CustomApiCrypto.sha256(token)), false);
        if (app == null || !app.isApiEnabled()) {
            throw new CustomApiUnauthorizedException("invalid access token");
        }
        if (app.getTokenExpireAt() == null || app.getTokenExpireAt().isBefore(LocalDateTime.now())) {
            throw new CustomApiUnauthorizedException("access token expired");
        }
        return app;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void validateSaveRequest(CustomApiAppSaveRequest request, boolean create) {
        if (request == null) {
            throw new JeecgBootException("request is required");
        }
        if (!create && request.getId() == null) {
            throw new JeecgBootException("id is required");
        }
        if (isBlank(request.getAppKey())) {
            throw new JeecgBootException("appKey is required");
        }
        if (isBlank(request.getCustomerCode())) {
            throw new JeecgBootException("customerCode is required");
        }
        if (isBlank(request.getCompanyCode())) {
            throw new JeecgBootException("companyCode is required");
        }
    }

    private ApiAppAgentGrantRequest toAgentGrantRequest(Long appId, CustomApiAppSaveRequest request) {
        List<String> agentCodes = request.getAgentCodes();
        if (agentCodes == null || agentCodes.isEmpty()) {
            agentCodes = List.of(request.getCompanyCode().trim());
        }
        ApiAppAgentGrantRequest grants = new ApiAppAgentGrantRequest();
        grants.setAppId(appId);
        grants.setAgentCodes(agentCodes);
        grants.setDefaultAgentCode(resolveDefaultAgentCode(request));
        return grants;
    }

    private String resolveDefaultAgentCode(CustomApiAppSaveRequest request) {
        return isBlank(request.getDefaultAgentCode())
                ? request.getCompanyCode().trim()
                : request.getDefaultAgentCode().trim();
    }
}
