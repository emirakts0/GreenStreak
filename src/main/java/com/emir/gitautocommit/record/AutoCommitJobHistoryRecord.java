package com.emir.gitautocommit.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoCommitJobHistoryRecord {
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
    private boolean isActive;
    private List<ExecutionRecord> executions;

    public AutoCommitJobHistoryRecord(String jobId, String branch, String repositoryUrl, String cronExpression, String commitCountRange, Date nextExecutionTime) {
        this.jobId = jobId;
        this.branch = branch;
        this.repositoryUrl = repositoryUrl;
        this.cronExpression = cronExpression;
        this.commitCountRange = commitCountRange;
        this.createdAt = new Date();
        this.isActive = true;
        this.nextExecutionTime = nextExecutionTime;

        this.executions = new ArrayList<>();
    }

    public void addExecution(
            Date executionTime,
            Date nextExecutionTime,
            int executionNumber,
            int commitsThisExecution,
            int totalCommits,
            boolean hasError,
            String errorMessage,
            String errorStackTrace
    ) {
        executions.add(new ExecutionRecord(
                executionTime,
                nextExecutionTime,
                executionNumber,
                commitsThisExecution,
                totalCommits,
                hasError,
                errorMessage,
                errorStackTrace
        ));
        this.lastExecutionTime = executionTime;
        this.nextExecutionTime = nextExecutionTime;
        this.totalExecutions = executionNumber;
        this.totalCommits = totalCommits;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutionRecord {
        private Date executionTime;
        private Date nextExecutionTime;
        private int executionNumber;
        private int commitsThisExecution;
        private int totalCommits;
        private boolean hasError;
        private String errorMessage;
        private String errorStackTrace;
    }
}
