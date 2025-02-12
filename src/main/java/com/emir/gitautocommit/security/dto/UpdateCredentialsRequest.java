package com.emir.gitautocommit.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCredentialsRequest {
    @NotBlank(message = "New username is required")
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String newUsername;

    @NotBlank(message = "New password is required")
    @Size(min = 4, message = "Password must be at least 6 characters long")
    private String newPassword;

    @NotBlank(message = "Current password is required")
    private String currentPassword;
} 