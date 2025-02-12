package com.emir.gitautocommit.security.controller;

import com.emir.gitautocommit.security.dto.AuthenticationRequest;
import com.emir.gitautocommit.security.dto.AuthenticationResponse;
import com.emir.gitautocommit.security.dto.UpdateCredentialsRequest;
import com.emir.gitautocommit.security.exception.AuthenticationException;
import com.emir.gitautocommit.security.model.AdminUser;
import com.emir.gitautocommit.security.service.AdminUserService;
import com.emir.gitautocommit.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminUserService adminUserService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        try {
            log.info("Login attempt for username: {}", request.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            AdminUser user = (AdminUser) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            log.info("Login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(token)
                    .isDefaultCredentials(adminUserService.isDefaultCredentials())
                    .build());
        } catch (BadCredentialsException e) {
            log.warn("Login failed for username: {}", request.getUsername());
            throw new AuthenticationException("Invalid username or password");
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/update-credentials")
    public ResponseEntity<AuthenticationResponse> updateCredentials(
            @Valid @RequestBody UpdateCredentialsRequest request,
            Authentication authentication) {
        try {
            AdminUser currentUser = (AdminUser) authentication.getPrincipal();
            log.info("Credential update attempt for user: {}", currentUser.getUsername());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(currentUser.getUsername(), request.getCurrentPassword())
            );

            adminUserService.updateCredentials(request.getNewUsername(), request.getNewPassword());

            String newToken = jwtService.generateToken(AdminUser.builder()
                    .username(request.getNewUsername())
                    .password(request.getNewPassword())
                    .build());

            log.info("Credentials successfully updated for user: {} -> {}",
                    currentUser.getUsername(), request.getNewUsername());
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(newToken)
                    .isDefaultCredentials(false)
                    .build());
        } catch (BadCredentialsException e) {
            log.warn("Credential update failed - incorrect current password for user: {}",
                    ((AdminUser) authentication.getPrincipal()).getUsername());
            throw new AuthenticationException("Current password is incorrect");
        } catch (Exception e) {
            log.error("Failed to update credentials", e);
            throw new AuthenticationException("Failed to update credentials: " + e.getMessage());
        }
    }
}