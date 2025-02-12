package com.emir.gitautocommit.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AutoCommitRequestDto {
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Username can only contain letters, numbers, hyphens, and underscores")
    private String username;

    @NotBlank(message = "Token is required")
    @Size(min = 8, message = "Token must be at least 8 characters long")
    private String token;

    @NotBlank(message = "Repository URL is required")
    @Pattern(
        regexp = "^(https://|git@)([\\w.-]+)(/|:)([\\w.-]+)/([\\w.-]+)(\\.git)?$",
        message = "Invalid Git repository URL format. Must be HTTPS or SSH format"
    )
    private String repositoryUrl;

    @NotBlank(message = "Branch name is required")
    @Pattern(
        regexp = "^[\\w.-]+$",
        message = "Branch name can only contain letters, numbers, dots, hyphens, and underscores"
    )
    private String branch;

    @NotBlank(message = "Commit count range is required")
    @Pattern(
        regexp = "^\\d+$|^\\d+-\\d+$",
        message = "Commit count range must be a single number or a range (e.g., '5' or '1-10')"
    )
    private String commitCountRange;

    @NotBlank(message = "Daily commit trigger time is required")
    @Pattern(
        regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
        message = "Daily commit trigger time must be in HH:mm format (24-hour)"
    )
    private String dailyCommitTriggerTime;

    @Min(value = 1, message = "Commit interval days must be at least 1")
    @Max(value = 365, message = "Commit interval days cannot exceed 365")
    private int commitIntervalDays;

    // Custom validation method for commit count range
    public void validateCommitCountRange() {
        if (commitCountRange != null && commitCountRange.contains("-")) {
            String[] parts = commitCountRange.split("-");
            if (parts.length == 2) {
                try {
                    int min = Integer.parseInt(parts[0]);
                    int max = Integer.parseInt(parts[1]);
                    if (min > max) {
                        throw new IllegalArgumentException("Minimum commit count cannot be greater than maximum");
                    }
                    if (min < 1) {
                        throw new IllegalArgumentException("Minimum commit count must be at least 1");
                    }
                    if (max > 100) {
                        throw new IllegalArgumentException("Maximum commit count cannot exceed 100");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid commit count range format");
                }
            }
        } else {
            try {
                int count = Integer.parseInt(commitCountRange);
                if (count < 1) {
                    throw new IllegalArgumentException("Commit count must be at least 1");
                }
                if (count > 100) {
                    throw new IllegalArgumentException("Commit count cannot exceed 100");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid commit count format");
            }
        }
    }
}
