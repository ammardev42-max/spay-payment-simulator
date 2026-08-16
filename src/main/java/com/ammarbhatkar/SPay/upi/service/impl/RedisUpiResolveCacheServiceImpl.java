package com.ammarbhatkar.SPay.upi.service.impl;

import com.ammarbhatkar.SPay.upi.dto.response.UpiHandleResponse;
import com.ammarbhatkar.SPay.upi.service.UpiResolveCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUpiResolveCacheServiceImpl implements UpiResolveCacheService {

    private static final Duration UPI_RESOLVE_CACHE_TTL = Duration.ofMinutes(10);
    private static final String UPI_RESOLVE_CACHE_PREFIX = "cache:upi-resolve:";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<UpiHandleResponse> get(String upiId) {
        try {
            String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey(upiId));

            if (cachedValue == null) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(cachedValue, UpiHandleResponse.class));
        } catch (RedisConnectionFailureException exception) {
            log.warn("Redis unavailable, skipping UPI resolve cache get for {}", upiId);
            return Optional.empty();
        } catch (JsonProcessingException exception) {
            log.warn("Invalid UPI resolve cache payload for {}", upiId);
            return Optional.empty();
        }
    }

    @Override
    public void put(String upiId, UpiHandleResponse response) {
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey(upiId),
                    objectMapper.writeValueAsString(response),
                    UPI_RESOLVE_CACHE_TTL
            );
        } catch (RedisConnectionFailureException exception) {
            log.warn("Redis unavailable, skipping UPI resolve cache put for {}", upiId);
        } catch (JsonProcessingException exception) {
            log.warn("Failed to serialize UPI resolve cache payload for {}", upiId);
        }
    }

    private String cacheKey(String upiId) {
        return UPI_RESOLVE_CACHE_PREFIX + upiId;
    }
}
