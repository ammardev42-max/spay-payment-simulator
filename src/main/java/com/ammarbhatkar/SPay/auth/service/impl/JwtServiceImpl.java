package com.ammarbhatkar.SPay.auth.service.impl;

import com.ammarbhatkar.SPay.auth.service.JwtService;
import com.ammarbhatkar.SPay.common.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${spay.jwt.secret-key}")
    private String secretKey;

    @Value("${spay.jwt.expiration-minutes}")
    private long expirationMinutes;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(String email, UUID userId, UserRole role) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .claim("user_id", userId.toString())
                .claim("role", role.name())
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public Claims verifyAccessToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }
}
