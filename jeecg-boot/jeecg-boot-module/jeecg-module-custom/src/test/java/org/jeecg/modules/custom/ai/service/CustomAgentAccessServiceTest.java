package org.jeecg.modules.custom.ai.service;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
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
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CustomAgentAccessServiceTest {

    private CustomCustomerMapper customerMapper;
    private CustomCustomerUserMapper customerUserMapper;
    private CustomUserAgentMapper userAgentMapper;
    private CustomAiAgentMapper agentMapper;
    private CustomApiAppAgentMapper appAgentMapper;
    private CustomAgentAccessService service;

    @BeforeEach
    void setUp() {
        customerMapper = mock(CustomCustomerMapper.class);
        customerUserMapper = mock(CustomCustomerUserMapper.class);
        userAgentMapper = mock(CustomUserAgentMapper.class);
        agentMapper = mock(CustomAiAgentMapper.class);
        appAgentMapper = mock(CustomApiAppAgentMapper.class);
        service = spy(new CustomAgentAccessService(
                customerMapper,
                customerUserMapper,
                userAgentMapper,
                agentMapper,
                appAgentMapper
        ));

        LoginUser user = new LoginUser().setId("user-1").setUsername("alice").setRoleCode("custom_user");
        doReturn(user).when(service).currentUser();
        when(customerUserMapper.selectList(any())).thenReturn(List.of(
                new CustomCustomerUser()
                        .setCustomerCode("CUSTOMER-A")
                        .setUserId("user-1")
                        .setUsername("alice")
                        .setEnabled(1)
        ));
        when(customerMapper.selectOne(any())).thenReturn(new CustomCustomer()
                .setCustomerCode("CUSTOMER-A")
                .setCustomerName("Customer A")
                .setEnabled(1));
    }

    @Test
    void rejectsUserWithoutAnyEnabledAgentGrant() {
        when(userAgentMapper.selectList(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.requireWebAgent(null))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("未授权智能体");
    }

    @Test
    void selectsTheOnlyEnabledAgent() {
        grantUserAgents(new CustomUserAgent()
                .setCustomerCode("CUSTOMER-A")
                .setUserId("user-1")
                .setAgentCode("CUSTOMS")
                .setIsDefault(1)
                .setEnabled(1));
        enableAgents(agent("CUSTOMS", "通用"));

        assertThat(service.requireWebAgent(null)).isEqualTo("CUSTOMS");
    }

    @Test
    void selectsDefaultWhenUserHasMultipleAgents() {
        grantUserAgents(
                grant("CUSTOMS", false),
                grant("ILLUMNA-CUSTOMS", true)
        );
        enableAgents(agent("CUSTOMS", "通用"), agent("ILLUMNA-CUSTOMS", "因美纳"));

        assertThat(service.requireWebAgent(null)).isEqualTo("ILLUMNA-CUSTOMS");
    }

    @Test
    void rejectsAgentOutsideCurrentUsersGrants() {
        grantUserAgents(grant("CUSTOMS", true));
        enableAgents(agent("CUSTOMS", "通用"));

        assertThatThrownBy(() -> service.requireWebAgent("ILLUMNA-CUSTOMS"))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("未授权");
    }

    @Test
    void exposesCustomerAndDefaultAgentForCurrentUser() {
        grantUserAgents(grant("CUSTOMS", true));
        enableAgents(agent("CUSTOMS", "通用"));

        assertThat(service.requireCurrentCustomer().customerCode()).isEqualTo("CUSTOMER-A");
        assertThat(service.listCurrentUserAgents())
                .singleElement()
                .satisfies(option -> {
                    assertThat(option.getAgentCode()).isEqualTo("CUSTOMS");
                    assertThat(option.getAgentName()).isEqualTo("通用");
                    assertThat(option.getDefaultAgent()).isTrue();
                });
    }

    @Test
    void apiAppCanOnlyRouteToAnEnabledGrantedAgent() {
        CustomApiApp app = new CustomApiApp().setId(7L).setCompanyCode("CUSTOMS");
        when(appAgentMapper.selectList(any())).thenReturn(List.of(
                new CustomApiAppAgent().setAppId(7L).setAgentCode("CUSTOMS").setIsDefault(1).setEnabled(1)
        ));
        enableAgents(agent("CUSTOMS", "通用"));

        assertThat(service.requireApiAgent(app, null)).isEqualTo("CUSTOMS");
        assertThatThrownBy(() -> service.requireApiAgent(app, "ILLUMNA-CUSTOMS"))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("未授权");
    }

    @Test
    void detectsSuperAdminByUsernameOrRole() {
        assertThat(service.isSuperAdmin(new LoginUser().setUsername("admin"))).isTrue();
        assertThat(service.isSuperAdmin(new LoginUser().setUsername("root").setRoleCode("user,admin"))).isTrue();
        assertThat(service.isSuperAdmin(new LoginUser().setUsername("alice").setRoleCode("user"))).isFalse();
    }

    private void grantUserAgents(CustomUserAgent... grants) {
        when(userAgentMapper.selectList(any())).thenReturn(List.of(grants));
    }

    private void enableAgents(CustomAiAgent... agents) {
        when(agentMapper.selectList(any())).thenReturn(List.of(agents));
    }

    private CustomUserAgent grant(String code, boolean defaultAgent) {
        return new CustomUserAgent()
                .setCustomerCode("CUSTOMER-A")
                .setUserId("user-1")
                .setAgentCode(code)
                .setIsDefault(defaultAgent ? 1 : 0)
                .setEnabled(1);
    }

    private CustomAiAgent agent(String code, String name) {
        return new CustomAiAgent()
                .setAgentCode(code)
                .setAgentName(name)
                .setEnabled(1);
    }
}
