package org.jeecg.modules.custom.ai.service;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.api.CommonAPI;
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
import org.jeecg.modules.custom.ai.vo.UserAgentGrantListItem;
import org.jeecg.modules.custom.ai.vo.UserAgentGrantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomAgentAccessServiceTest {

    private CustomCustomerMapper customerMapper;
    private CustomCustomerUserMapper customerUserMapper;
    private CustomUserAgentMapper userAgentMapper;
    private CustomAiAgentMapper agentMapper;
    private CustomApiAppAgentMapper appAgentMapper;
    private CommonAPI commonAPI;
    private CustomAgentAccessService service;

    @BeforeEach
    void setUp() {
        customerMapper = mock(CustomCustomerMapper.class);
        customerUserMapper = mock(CustomCustomerUserMapper.class);
        userAgentMapper = mock(CustomUserAgentMapper.class);
        agentMapper = mock(CustomAiAgentMapper.class);
        appAgentMapper = mock(CustomApiAppAgentMapper.class);
        commonAPI = mock(CommonAPI.class);
        service = spy(new CustomAgentAccessService(
                customerMapper,
                customerUserMapper,
                userAgentMapper,
                agentMapper,
                appAgentMapper,
                commonAPI
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

    @Test
    void rejectsGrantReplacementWhenUserIsNotEnabledForTheSameCustomer() {
        asAdmin();
        enableAgents(agent("CUSTOMS", "通用"));
        when(customerUserMapper.selectOne(any())).thenReturn(null);
        UserAgentGrantRequest request = grantRequest("CUSTOMER-A", "user-2", List.of("CUSTOMS"), "CUSTOMS");

        assertThatThrownBy(() -> service.replaceUserAgents(request))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("未绑定当前客户");
        verify(userAgentMapper, never()).delete(any());
    }

    @Test
    void rejectsCustomerUserSaveWhenCustomerIsDisabled() {
        asAdmin();
        when(customerMapper.selectOne(any())).thenReturn(null);
        CustomCustomerUser relation = new CustomCustomerUser()
                .setCustomerCode("DISABLED")
                .setUserId("user-2")
                .setUsername("bob");

        assertThatThrownBy(() -> service.replaceCustomerUser(relation))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("客户不存在或已停用");
        verify(customerUserMapper, never()).delete(any());
    }

    @Test
    void aggregatesUserGrantListWithCustomerUserAgentAndDefaultDisplayFields() {
        asAdmin();
        LocalDateTime relationUpdated = LocalDateTime.of(2026, 7, 14, 9, 0);
        LocalDateTime grantUpdated = relationUpdated.plusMinutes(1);
        when(customerUserMapper.selectList(any())).thenReturn(List.of(
                new CustomCustomerUser().setId(18L).setCustomerCode("CUSTOMER-A")
                        .setUserId("user-1").setUsername("alice").setEnabled(1).setUpdatedAt(relationUpdated)
        ));
        when(customerMapper.selectList(any())).thenReturn(List.of(
                new CustomCustomer().setCustomerCode("CUSTOMER-A").setCustomerName("Customer A").setEnabled(1)
        ));
        when(userAgentMapper.selectList(any())).thenReturn(List.of(
                grant("CUSTOMS", true).setUpdatedAt(grantUpdated),
                grant("ILLUMNA-CUSTOMS", false).setUpdatedAt(relationUpdated)
        ));
        enableAgents(agent("CUSTOMS", "通用"), agent("ILLUMNA-CUSTOMS", "因美纳"));
        when(commonAPI.getUserByName("alice")).thenReturn(
                new LoginUser().setId("user-1").setUsername("alice").setRealname("Alice Zhang")
        );

        List<UserAgentGrantListItem> rows = service.listUserGrants("CUSTOMER-A", "ali", "CUSTOMS");

        assertThat(rows).singleElement().satisfies(row -> {
            assertThat(row.getId()).isEqualTo(18L);
            assertThat(row.getCustomerCode()).isEqualTo("CUSTOMER-A");
            assertThat(row.getCustomerName()).isEqualTo("Customer A");
            assertThat(row.getUserId()).isEqualTo("user-1");
            assertThat(row.getUsername()).isEqualTo("alice");
            assertThat(row.getRealname()).isEqualTo("Alice Zhang");
            assertThat(row.getAgentCodes()).containsExactly("CUSTOMS", "ILLUMNA-CUSTOMS");
            assertThat(row.getAgentNames()).containsExactly("通用", "因美纳");
            assertThat(row.getDefaultAgentCode()).isEqualTo("CUSTOMS");
            assertThat(row.getDefaultAgentName()).isEqualTo("通用");
            assertThat(row.getUpdatedAt()).isEqualTo(grantUpdated);
        });
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

    private void asAdmin() {
        doReturn(new LoginUser().setId("admin-id").setUsername("admin").setRoleCode("admin"))
                .when(service).currentUser();
    }

    private UserAgentGrantRequest grantRequest(String customerCode, String userId,
                                               List<String> agentCodes, String defaultCode) {
        UserAgentGrantRequest request = new UserAgentGrantRequest();
        request.setCustomerCode(customerCode);
        request.setUserId(userId);
        request.setAgentCodes(agentCodes);
        request.setDefaultAgentCode(defaultCode);
        return request;
    }
}
