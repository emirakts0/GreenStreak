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
public class StatusDto {
    private boolean hasActiveJob;
    private String jobId;
    private String branch;
    private String repositoryUrl;
    private String cronExpression;
    private String commitCountRange;
    private Date createdAt;
    private Date lastExecutionTime;
    private Date nextExecutionTime;
    private int totalExecutions;
    private int totalCommits;
    private boolean lastExecutionHadError;
    private String lastErrorMessage;
} 