package com.ammarbhatkar.SPay.common.lock;

import java.time.Duration;
import java.util.function.Supplier;

public interface DistributedLockService {

    <T> T executeWithLock(String lockKey, Duration ttl, Supplier<T> action);
}
