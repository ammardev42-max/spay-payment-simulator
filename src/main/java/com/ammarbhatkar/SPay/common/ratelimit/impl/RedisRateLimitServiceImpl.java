package com.ammarbhatkar.SPay.common.ratelimit.impl;

import com.ammarbhatkar.SPay.common.exception.RateLimitException;
import com.ammarbhatkar.SPay.common.ratelimit.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRateLimitServiceImpl implements RateLimitService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void checkLimit(String key, int maxRequests, Duration window) {
        try {
            Long requestCount = stringRedisTemplate.opsForValue().increment(key);

            if (requestCount != null && requestCount == 1L) {
                stringRedisTemplate.expire(key, window);
            }

            if (requestCount != null && requestCount > maxRequests) {
                throw new RateLimitException(
                        "Too many payment attempts. Please try again after a minute.",
                        window.toSeconds()
                );
            }
        } catch (RedisConnectionFailureException exception) {
            log.warn("Redis unavailable, allowing request without rate limit for key {}", key);
        }
    }
}
