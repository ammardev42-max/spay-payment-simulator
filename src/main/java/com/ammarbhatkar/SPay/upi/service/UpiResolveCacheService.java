package com.ammarbhatkar.SPay.upi.service;

import com.ammarbhatkar.SPay.upi.dto.response.UpiHandleResponse;

import java.util.Optional;

public interface UpiResolveCacheService {

    Optional<UpiHandleResponse> get(String upiId);

    void put(String upiId, UpiHandleResponse response);
}
