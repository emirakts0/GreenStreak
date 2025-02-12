package com.emir.gitautocommit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoCommitResponseDto {
    private String jobId;
    private String branch;
    private String repositoryUrl;
    private String cronExpression;
    private Date createdAt;
    private Date nextExecutionTime;
    private boolean isActive;
}
