package org.jeecg.modules.custom.api.service;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.exception.CustomApiRateLimitException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class CustomApiRateLimiter {
    private static final RedisScript<List> TOKEN_BUCKET = new DefaultRedisScript<>("""
            local capacity = tonumber(ARGV[1])
            local refill = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local requested = tonumber(ARGV[4])
            local state = redis.call('HMGET', KEYS[1], 'tokens', 'timestamp')
            local tokens = tonumber(state[1])
            local timestamp = tonumber(state[2])
            if tokens == nil then tokens = capacity end
            if timestamp == nil then timestamp = now end
            tokens = math.min(capacity, tokens + math.max(0, now - timestamp) * refill)
            local allowed = 0
            local retry = 0
            if tokens >= requested then
              tokens = tokens - requested
              allowed = 1
            else
              retry = math.max(1, math.ceil((requested - tokens) / refill))
            end
            redis.call('HMSET', KEYS[1], 'tokens', tokens, 'timestamp', now)
            redis.call('EXPIRE', KEYS[1], 120)
            return {allowed, retry}
            """, List.class);

    private final StringRedisTemplate redisTemplate;

    public CustomApiRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void check(CustomApiApp app, String endpointGroup) {
        if (app == null || app.getId() == null || app.getRateLimit() == null || app.getRateLimit() <= 0) {
            return;
        }
        int capacity = app.getRateLimit();
        double refillPerSecond = capacity / 60.0d;
        String key = "custom:api:rate:" + app.getId() + ":" + endpointGroup;
        try {
            List<?> result = redisTemplate.execute(TOKEN_BUCKET, List.of(key),
                    String.valueOf(capacity),
                    String.valueOf(refillPerSecond),
                    String.valueOf(Instant.now().getEpochSecond()),
                    "1");
            if (result != null && !result.isEmpty() && number(result.get(0)) == 0L) {
                long retry = result.size() > 1 ? number(result.get(1)) : 1L;
                throw new CustomApiRateLimitException(retry);
            }
        } catch (CustomApiRateLimitException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Custom API rate limiter unavailable, appId={}, group={}", app.getId(), endpointGroup, e);
        }
    }

    private long number(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
    }
}
