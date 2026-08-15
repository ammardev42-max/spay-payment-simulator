package com.ammarbhatkar.SPay.common.security;

import com.ammarbhatkar.SPay.common.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@Getter
@Setter
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {

    private UUID userId;
    private String email;
    private UserRole role;
}