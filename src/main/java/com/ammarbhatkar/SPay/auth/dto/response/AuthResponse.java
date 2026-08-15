package com.ammarbhatkar.SPay.auth.dto.response;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String fullName,
        String email,
        String phoneNumber,
        String accessToken,
        String tokenType
) {
}
