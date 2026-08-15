package com.ammarbhatkar.SPay.auth.service;

import com.ammarbhatkar.SPay.common.enums.UserRole;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(String email, UUID userId, UserRole role);

    Claims verifyAccessToken(String accessToken);
}

