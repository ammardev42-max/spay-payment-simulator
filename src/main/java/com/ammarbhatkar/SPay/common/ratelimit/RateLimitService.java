package com.ammarbhatkar.SPay.common.ratelimit;

import java.time.Duration;

public interface RateLimitService {

    void checkLimit(String key, int maxRequests, Duration window);
}
