package com.ammarbhatkar.SPay.user.repository;

import com.ammarbhatkar.SPay.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface AppUserRepository  extends JpaRepository {
    Optional<AppUser> findByEmail (String email);
    boolean existsByEmail (String email);
    boolean existsByPhoneNumber (String phoneNumber);
}
