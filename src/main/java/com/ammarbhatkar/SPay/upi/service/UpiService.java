package com.ammarbhatkar.SPay.upi.service;

import com.ammarbhatkar.SPay.upi.dto.request.CreateUpiHandleRequest;
import com.ammarbhatkar.SPay.upi.dto.response.UpiHandleResponse;

import java.util.List;

public interface UpiService {

    UpiHandleResponse createHandle(CreateUpiHandleRequest request);

    List<UpiHandleResponse> getMyHandles();

    UpiHandleResponse resolve(String upiId);
}