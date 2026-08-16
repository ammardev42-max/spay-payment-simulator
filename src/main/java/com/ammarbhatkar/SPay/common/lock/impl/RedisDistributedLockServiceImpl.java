package com.ammarbhatkar.SPay.common.lock.impl;

import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.lock.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDistributedLockServiceImpl implements DistributedLockService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public <T> T executeWithLock(String lockKey, Duration ttl, Supplier<T> action) {
        String lockValue = UUID.randomUUID().toString();

        try {
            Boolean locked = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, ttl);

            if (!Boolean.TRUE.equals(locked)) {
                throw new BusinessRuleViolationException(
                        "PAYMENT_ALREADY_IN_PROGRESS",
                        "Another payment is already using this bank account. Please try again."
                );
            }

            try {
                return action.get();
            } finally {
                releaseLock(lockKey, lockValue);
            }
        } catch (RedisConnectionFailureException exception) {
            log.warn("Redis unavailable, continuing without distributed lock for key {}", lockKey);
            return action.get();
        }
    }

    private void releaseLock(String lockKey, String lockValue) {
        String currentValue = stringRedisTemplate.opsForValue().get(lockKey);

        if (Objects.equals(currentValue, lockValue)) {
            stringRedisTemplate.delete(lockKey);
        }
    }
}
