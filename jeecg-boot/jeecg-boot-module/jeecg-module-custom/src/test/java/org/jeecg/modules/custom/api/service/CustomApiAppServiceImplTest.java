package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.exception.CustomApiRateLimitException;
import org.jeecg.modules.custom.api.service.impl.CustomApiAppServiceImpl;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.jeecg.modules.custom.api.vo.CustomApiAppSaveRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CustomApiAppServiceImplTest {

    @Test
    void createsAppAndAgentGrantsAtomicallyThroughOneServiceCall() throws Exception {
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        CustomApiAppServiceImpl service = spy(new CustomApiAppServiceImpl());
        setField(service, "agentAccessService", accessService);
        doReturn(false).when(service).appKeyExists("client-a", null);
        doAnswer(invocation -> {
            ((CustomApiApp) invocation.getArgument(0)).setId(7L);
            return true;
        }).when(service).save(any(CustomApiApp.class));
        CustomApiAppSaveRequest request = new CustomApiAppSaveRequest();
        request.setAppKey("client-a");
        request.setCustomerCode("CUSTOMER-A");
        request.setCompanyCode("CUSTOMS");
        request.setAgentCodes(List.of("CUSTOMS", "ILLUMNA-CUSTOMS"));
        request.setDefaultAgentCode("CUSTOMS");

        service.createApp(request);

        verify(accessService).replaceApiAppAgents(argThat(grants ->
                Long.valueOf(7L).equals(grants.getAppId())
                        && grants.getAgentCodes().equals(List.of("CUSTOMS", "ILLUMNA-CUSTOMS"))
                        && "CUSTOMS".equals(grants.getDefaultAgentCode())
        ));
    }

    @Test
    void rateLimitsResolvedAppBeforeRejectingWrongSecret() throws Exception {
        CustomApiRateLimiter rateLimiter = mock(CustomApiRateLimiter.class);
        CustomApiAppServiceImpl service = spy(new CustomApiAppServiceImpl());
        setField(service, "rateLimiter", rateLimiter);
        CustomApiApp app = new CustomApiApp()
                .setId(9L)
                .setAppKey("client-a")
                .setAppSecretHash(CustomApiCrypto.sha256("correct-secret"))
                .setEnabled(1)
                .setRateLimit(10);
        doReturn(app).when(service).getOne(any(), eq(false));
        doThrow(new CustomApiRateLimitException(3)).when(rateLimiter).check(app, "token");
        AuthTokenRequest request = new AuthTokenRequest();
        request.setAppKey("client-a");
        request.setAppSecret("wrong-secret");

        assertThatThrownBy(() -> service.issueToken(request))
                .isInstanceOf(CustomApiRateLimitException.class)
                .satisfies(error -> org.assertj.core.api.Assertions.assertThat(
                        ((CustomApiRateLimitException) error).getRetryAfterSeconds()).isEqualTo(3));
        verify(rateLimiter).check(app, "token");
    }

    @Test
    void deletesAgentGrantsWithTheApiApp() throws Exception {
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        CustomApiAppServiceImpl service = spy(new CustomApiAppServiceImpl());
        setField(service, "agentAccessService", accessService);
        doReturn(true).when(service).removeById(7L);

        service.deleteApp(7L);

        verify(accessService).deleteApiAppAgents(7L);
        verify(service).removeById(7L);
        verifyNoMoreInteractions(accessService);
    }

    @Test
    void batchDeleteCleansEveryAppsAgentGrants() throws Exception {
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        CustomApiAppServiceImpl service = spy(new CustomApiAppServiceImpl());
        setField(service, "agentAccessService", accessService);
        doReturn(true).when(service).removeByIds(List.of(7L, 8L));

        service.deleteApps(List.of(7L, 8L));

        verify(accessService).deleteApiAppAgents(7L);
        verify(accessService).deleteApiAppAgents(8L);
        verify(service).removeByIds(List.of(7L, 8L));
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
