package com.ammarbhatkar.SPay.user.mapper;

import com.ammarbhatkar.SPay.auth.dto.response.AuthResponse;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "tokenType", ignore = true)
    AuthResponse toAuthResponse(AppUser appUser);

    default AuthResponse toAuthResponse(AppUser appUser, String accessToken) {
        return new AuthResponse(
                appUser.getId(),
                appUser.getFullName(),
                appUser.getEmail(),
                appUser.getPhoneNumber(),
                accessToken,
                "Bearer"
        );
    }
}