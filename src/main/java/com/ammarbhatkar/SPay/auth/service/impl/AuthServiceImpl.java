package com.ammarbhatkar.SPay.auth.service.impl;

import com.ammarbhatkar.SPay.auth.dto.request.LoginRequest;
import com.ammarbhatkar.SPay.auth.dto.request.RegisterRequest;
import com.ammarbhatkar.SPay.auth.dto.response.AuthResponse;
import com.ammarbhatkar.SPay.auth.service.AuthService;
import com.ammarbhatkar.SPay.common.enums.UserRole;
import com.ammarbhatkar.SPay.common.enums.UserStatus;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.DuplicateResourceException;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import com.ammarbhatkar.SPay.user.mapper.UserMapper;
import com.ammarbhatkar.SPay.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        String normalizedPhoneNumber = request.phoneNumber().trim();

        if (appUserRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "DUPLICATE_USER_EMAIL",
                    "User with email already exists: " + normalizedEmail
            );
        }

        if (appUserRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            throw new DuplicateResourceException(
                    "DUPLICATE_USER_PHONE",
                    "User with phone number already exists: " + normalizedPhoneNumber
            );
        }

        AppUser appUser = AppUser.builder()
                .fullName(request.fullName().trim())
                .email(normalizedEmail)
                .phoneNumber(normalizedPhoneNumber)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        AppUser savedUser = appUserRepository.save(appUser);

        log.info("User registered successfully, userId={}", savedUser.getId());

        return userMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        throw new BusinessRuleViolationException(
                "LOGIN_NOT_IMPLEMENTED",
                "Login will be implemented after register"
        );
    }
}
