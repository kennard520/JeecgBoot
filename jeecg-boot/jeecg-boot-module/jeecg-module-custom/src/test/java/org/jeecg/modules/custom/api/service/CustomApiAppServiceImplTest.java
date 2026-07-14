package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.exception.CustomApiRateLimitException;
import org.jeecg.modules.custom.api.service.impl.CustomApiAppServiceImpl;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CustomApiAppServiceImplTest {

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
