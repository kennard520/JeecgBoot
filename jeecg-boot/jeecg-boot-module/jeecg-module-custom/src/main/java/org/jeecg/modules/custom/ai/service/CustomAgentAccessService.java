package org.jeecg.modules.custom.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.security.utils.SecureUtil;
import org.jeecg.modules.custom.ai.entity.CustomAiAgent;
import org.jeecg.modules.custom.ai.entity.CustomApiAppAgent;
import org.jeecg.modules.custom.ai.entity.CustomCustomer;
import org.jeecg.modules.custom.ai.entity.CustomCustomerUser;
import org.jeecg.modules.custom.ai.entity.CustomUserAgent;
import org.jeecg.modules.custom.ai.mapper.CustomAiAgentMapper;
import org.jeecg.modules.custom.ai.mapper.CustomApiAppAgentMapper;
import org.jeecg.modules.custom.ai.mapper.CustomCustomerMapper;
import org.jeecg.modules.custom.ai.mapper.CustomCustomerUserMapper;
import org.jeecg.modules.custom.ai.mapper.CustomUserAgentMapper;
import org.jeecg.modules.custom.ai.vo.AgentOptionResponse;
import org.jeecg.modules.custom.ai.vo.ApiAppAgentGrantRequest;
import org.jeecg.modules.custom.ai.vo.CurrentCustomer;
import org.jeecg.modules.custom.ai.vo.UserAgentGrantRequest;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomAgentAccessService {
    private final CustomCustomerMapper customerMapper;
    private final CustomCustomerUserMapper customerUserMapper;
    private final CustomUserAgentMapper userAgentMapper;
    private final CustomAiAgentMapper agentMapper;
    private final CustomApiAppAgentMapper appAgentMapper;

    public CustomAgentAccessService(CustomCustomerMapper customerMapper,
                                    CustomCustomerUserMapper customerUserMapper,
                                    CustomUserAgentMapper userAgentMapper,
                                    CustomAiAgentMapper agentMapper,
                                    CustomApiAppAgentMapper appAgentMapper) {
        this.customerMapper = customerMapper;
        this.customerUserMapper = customerUserMapper;
        this.userAgentMapper = userAgentMapper;
        this.agentMapper = agentMapper;
        this.appAgentMapper = appAgentMapper;
    }

    public CurrentCustomer requireCurrentCustomer() {
        LoginUser user = requireLoginUser();
        if (isSuperAdmin(user)) {
            return new CurrentCustomer(null, user.getId(), user.getUsername(), true);
        }
        List<CustomCustomerUser> relations = customerUserMapper.selectList(
                new LambdaQueryWrapper<CustomCustomerUser>()
                        .eq(CustomCustomerUser::getUserId, user.getId())
                        .eq(CustomCustomerUser::getEnabled, 1)
        );
        if (relations == null || relations.isEmpty()) {
            throw new JeecgBootException("当前用户未绑定客户");
        }
        if (relations.size() != 1) {
            throw new JeecgBootException("当前用户存在多个有效客户关系，请联系管理员");
        }
        CustomCustomerUser relation = relations.get(0);
        CustomCustomer customer = customerMapper.selectOne(
                new LambdaQueryWrapper<CustomCustomer>()
                        .eq(CustomCustomer::getCustomerCode, relation.getCustomerCode())
                        .eq(CustomCustomer::getEnabled, 1)
        );
        if (customer == null || !customer.isEnabledCustomer()) {
            throw new JeecgBootException("当前客户已停用或不存在");
        }
        return new CurrentCustomer(relation.getCustomerCode(), user.getId(), user.getUsername(), false);
    }

    public List<AgentOptionResponse> listCurrentUserAgents() {
        CurrentCustomer current = requireCurrentCustomer();
        if (current.superAdmin()) {
            return toOptions(enabledAgents(), Set.of(), null);
        }
        List<CustomUserAgent> grants = userAgentMapper.selectList(
                new LambdaQueryWrapper<CustomUserAgent>()
                        .eq(CustomUserAgent::getCustomerCode, current.customerCode())
                        .eq(CustomUserAgent::getUserId, current.userId())
                        .eq(CustomUserAgent::getEnabled, 1)
        );
        Map<String, CustomUserAgent> byCode = grants == null ? Map.of() : grants.stream()
                .collect(Collectors.toMap(CustomUserAgent::getAgentCode, grant -> grant, (left, right) -> left, LinkedHashMap::new));
        String defaultCode = byCode.values().stream()
                .filter(grant -> Integer.valueOf(1).equals(grant.getIsDefault()))
                .map(CustomUserAgent::getAgentCode)
                .findFirst()
                .orElse(null);
        return toOptions(enabledAgents(), byCode.keySet(), defaultCode);
    }

    public String requireWebAgent(String requestedAgentCode) {
        List<AgentOptionResponse> options = listCurrentUserAgents();
        if (options.isEmpty()) {
            throw new JeecgBootException("当前用户未授权智能体，请联系管理员");
        }
        String requested = trimToNull(requestedAgentCode);
        if (requested != null) {
            return options.stream()
                    .filter(option -> requested.equals(option.getAgentCode()))
                    .map(AgentOptionResponse::getAgentCode)
                    .findFirst()
                    .orElseThrow(() -> new JeecgBootException("请求的智能体未授权或已停用"));
        }
        if (options.size() == 1) {
            return options.get(0).getAgentCode();
        }
        return options.stream()
                .filter(option -> Boolean.TRUE.equals(option.getDefaultAgent()))
                .map(AgentOptionResponse::getAgentCode)
                .findFirst()
                .orElseThrow(() -> new JeecgBootException("存在多个智能体，请选择一个智能体"));
    }

    public String requireApiAgent(CustomApiApp app, String requestedCompanyCode) {
        if (app == null || app.getId() == null) {
            throw new JeecgBootException("API 应用不存在");
        }
        List<CustomApiAppAgent> grants = appAgentMapper.selectList(
                new LambdaQueryWrapper<CustomApiAppAgent>()
                        .eq(CustomApiAppAgent::getAppId, app.getId())
                        .eq(CustomApiAppAgent::getEnabled, 1)
        );
        Map<String, CustomApiAppAgent> byCode = grants == null ? Map.of() : grants.stream()
                .collect(Collectors.toMap(CustomApiAppAgent::getAgentCode, grant -> grant, (left, right) -> left, LinkedHashMap::new));
        Map<String, CustomAiAgent> enabled = enabledAgents().stream()
                .collect(Collectors.toMap(CustomAiAgent::getAgentCode, agent -> agent));
        List<CustomApiAppAgent> usable = byCode.values().stream()
                .filter(grant -> enabled.containsKey(grant.getAgentCode()))
                .toList();
        if (usable.isEmpty()) {
            throw new JeecgBootException("API 应用未授权智能体");
        }
        String requested = trimToNull(requestedCompanyCode);
        if (requested != null) {
            return usable.stream()
                    .filter(grant -> requested.equals(grant.getAgentCode()))
                    .map(CustomApiAppAgent::getAgentCode)
                    .findFirst()
                    .orElseThrow(() -> new JeecgBootException("请求的智能体未授权或已停用"));
        }
        if (usable.size() == 1) {
            return usable.get(0).getAgentCode();
        }
        return usable.stream()
                .filter(grant -> Integer.valueOf(1).equals(grant.getIsDefault()))
                .map(CustomApiAppAgent::getAgentCode)
                .findFirst()
                .orElseThrow(() -> new JeecgBootException("API 应用存在多个智能体，请指定 companyCode"));
    }

    public boolean isSuperAdmin(LoginUser user) {
        if (user == null) {
            return false;
        }
        if ("admin".equalsIgnoreCase(trimToNull(user.getUsername()))) {
            return true;
        }
        String roles = trimToNull(user.getRoleCode());
        if (roles == null) {
            return false;
        }
        for (String role : roles.split(",")) {
            if ("admin".equalsIgnoreCase(role.trim()) || "super_admin".equalsIgnoreCase(role.trim())) {
                return true;
            }
        }
        return false;
    }

    public void requireSuperAdmin() {
        if (!isSuperAdmin(requireLoginUser())) {
            throw new JeecgBootException("仅超级管理员可执行此操作");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomUserAgent> replaceUserAgents(UserAgentGrantRequest request) {
        requireSuperAdmin();
        if (request == null || isBlank(request.getCustomerCode()) || isBlank(request.getUserId())) {
            throw new JeecgBootException("customerCode 和 userId 不能为空");
        }
        requireEnabledCustomer(request.getCustomerCode());
        List<String> codes = normalizeAgentCodes(request.getAgentCodes());
        String defaultCode = resolveDefault(codes, request.getDefaultAgentCode());
        requireEnabledAgents(codes);
        LocalDateTime now = LocalDateTime.now();
        userAgentMapper.delete(new LambdaQueryWrapper<CustomUserAgent>().eq(CustomUserAgent::getUserId, request.getUserId()));
        List<CustomUserAgent> saved = new ArrayList<>();
        for (String code : codes) {
            CustomUserAgent grant = new CustomUserAgent()
                    .setCustomerCode(request.getCustomerCode().trim())
                    .setUserId(request.getUserId().trim())
                    .setAgentCode(code)
                    .setIsDefault(code.equals(defaultCode) ? 1 : 0)
                    .setEnabled(1)
                    .setCreatedAt(now)
                    .setUpdatedAt(now);
            userAgentMapper.insert(grant);
            saved.add(grant);
        }
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomApiAppAgent> replaceApiAppAgents(ApiAppAgentGrantRequest request) {
        requireSuperAdmin();
        if (request == null || request.getAppId() == null) {
            throw new JeecgBootException("appId 不能为空");
        }
        List<String> codes = normalizeAgentCodes(request.getAgentCodes());
        String defaultCode = resolveDefault(codes, request.getDefaultAgentCode());
        requireEnabledAgents(codes);
        LocalDateTime now = LocalDateTime.now();
        appAgentMapper.delete(new LambdaQueryWrapper<CustomApiAppAgent>().eq(CustomApiAppAgent::getAppId, request.getAppId()));
        List<CustomApiAppAgent> saved = new ArrayList<>();
        for (String code : codes) {
            CustomApiAppAgent grant = new CustomApiAppAgent()
                    .setAppId(request.getAppId())
                    .setAgentCode(code)
                    .setIsDefault(code.equals(defaultCode) ? 1 : 0)
                    .setEnabled(1)
                    .setCreatedAt(now)
                    .setUpdatedAt(now);
            appAgentMapper.insert(grant);
            saved.add(grant);
        }
        return saved;
    }

    LoginUser currentUser() {
        return SecureUtil.currentUser();
    }

    private LoginUser requireLoginUser() {
        LoginUser user;
        try {
            user = currentUser();
        } catch (Exception e) {
            throw new JeecgBootException("用户未登录");
        }
        if (user == null || isBlank(user.getId()) || isBlank(user.getUsername())) {
            throw new JeecgBootException("用户未登录");
        }
        return user;
    }

    private List<CustomAiAgent> enabledAgents() {
        List<CustomAiAgent> agents = agentMapper.selectList(
                new LambdaQueryWrapper<CustomAiAgent>()
                        .eq(CustomAiAgent::getEnabled, 1)
                        .orderByAsc(CustomAiAgent::getSortOrder, CustomAiAgent::getAgentCode)
        );
        return agents == null ? List.of() : agents.stream().filter(CustomAiAgent::isEnabledAgent).toList();
    }

    private List<AgentOptionResponse> toOptions(List<CustomAiAgent> agents, Set<String> allowed, String defaultCode) {
        boolean all = allowed.isEmpty() && isSuperAdmin(currentUser());
        return agents.stream()
                .filter(agent -> all || allowed.contains(agent.getAgentCode()))
                .map(agent -> new AgentOptionResponse()
                        .setAgentCode(agent.getAgentCode())
                        .setAgentName(agent.getAgentName())
                        .setDescription(agent.getDescription())
                        .setDefaultAgent(agent.getAgentCode().equals(defaultCode)))
                .toList();
    }

    private void requireEnabledCustomer(String customerCode) {
        CustomCustomer customer = customerMapper.selectOne(new LambdaQueryWrapper<CustomCustomer>()
                .eq(CustomCustomer::getCustomerCode, customerCode.trim())
                .eq(CustomCustomer::getEnabled, 1));
        if (customer == null) {
            throw new JeecgBootException("客户不存在或已停用");
        }
    }

    private void requireEnabledAgents(List<String> codes) {
        Set<String> enabled = enabledAgents().stream().map(CustomAiAgent::getAgentCode).collect(Collectors.toSet());
        if (!enabled.containsAll(codes)) {
            throw new JeecgBootException("授权列表包含不存在或已停用的智能体");
        }
    }

    private List<String> normalizeAgentCodes(List<String> values) {
        if (values == null) {
            return List.of();
        }
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        for (String value : values) {
            String code = trimToNull(value);
            if (code != null) {
                codes.add(code.toUpperCase(Locale.ROOT));
            }
        }
        return List.copyOf(codes);
    }

    private String resolveDefault(List<String> codes, String requested) {
        if (codes.isEmpty()) {
            return null;
        }
        String defaultCode = trimToNull(requested);
        if (defaultCode == null && codes.size() == 1) {
            return codes.get(0);
        }
        if (defaultCode == null || !codes.contains(defaultCode.toUpperCase(Locale.ROOT))) {
            throw new JeecgBootException("多个智能体必须且只能设置一个默认项");
        }
        return defaultCode.toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
