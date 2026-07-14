package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.exception.CustomApiRateLimitException;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class CustomApiRateLimiterTest {

    @Test
    void rejectsExhaustedBucketWithRetryAfter() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        doReturn(List.of(0L, 2L)).when(redis).execute(any(RedisScript.class), anyList(), any(Object[].class));
        CustomApiRateLimiter limiter = new CustomApiRateLimiter(redis);
        CustomApiApp app = new CustomApiApp().setId(12L).setRateLimit(60);

        assertThatThrownBy(() -> limiter.check(app, "poll"))
                .isInstanceOfSatisfying(CustomApiRateLimitException.class,
                        error -> assertThat(error.getRetryAfterSeconds()).isEqualTo(2L));
    }
}
