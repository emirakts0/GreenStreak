package com.emir.gitautocommit.security.service;

import com.emir.gitautocommit.security.exception.AuthenticationException;
import com.emir.gitautocommit.security.model.AdminUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminUserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private AdminUser adminUser;

    @Value("${admin.default.username:admin}")
    private String defaultUsername;

    @Value("${admin.default.password:admin}")
    private String defaultPassword;

    public AdminUserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        try {
            this.adminUser = AdminUser.builder()
                    .username(defaultUsername)
                    .password(passwordEncoder.encode(defaultPassword))
                    .build();
            log.info("Default admin user initialized with username: {}", defaultUsername);
        } catch (Exception e) {
            log.error("Failed to initialize admin user", e);
            throw new AuthenticationException("Failed to initialize admin user", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals(adminUser.getUsername())) {
            return adminUser;
        }
        log.warn("Failed login attempt with username: {}", username);
        throw new UsernameNotFoundException("User not found: " + username);
    }

    public void updateCredentials(String newUsername, String newPassword) {
        try {
            if (newUsername == null || newUsername.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            this.adminUser = AdminUser.builder()
                    .username(newUsername)
                    .password(passwordEncoder.encode(newPassword))
                    .build();
            log.info("Admin credentials updated successfully for username: {}", newUsername);
        } catch (Exception e) {
            log.error("Failed to update admin credentials", e);
            throw new AuthenticationException("Failed to update admin credentials", e);
        }
    }

    public boolean isDefaultCredentials() {
        return adminUser.getUsername().equals(defaultUsername);
    }
} 