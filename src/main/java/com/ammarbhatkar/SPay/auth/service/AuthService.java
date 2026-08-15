package com.ammarbhatkar.SPay.auth.service;

import com.ammarbhatkar.SPay.auth.dto.request.LoginRequest;
import com.ammarbhatkar.SPay.auth.dto.request.RegisterRequest;
import com.ammarbhatkar.SPay.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest loginRequest);
}
