package com.emir.gitautocommit.service;

import com.emir.gitautocommit.record.AutoCommitJobHistoryRecord;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class AutoCommitJobHistoryService {
    private final Map<String, AutoCommitJobHistoryRecord> jobHistoryMap = new HashMap<>();

    public void addJobHistory(String jobId, String branch, String repositoryUrl, String cronExpression, String commitCountRange, Date nextExecutionTime) {
        if (jobHistoryMap.containsKey(jobId)) {
            throw new IllegalArgumentException("Job history with this ID already exists: " + jobId);
        }
        jobHistoryMap.put(jobId, new AutoCommitJobHistoryRecord(jobId, branch, repositoryUrl, cronExpression, commitCountRange, nextExecutionTime));
    }

    public void addExecution(String jobId,
                             Date executionTime,
                             Date nextExecutionTime,
                             int executionNumber,
                             int commitsThisExecution,
                             int totalCommits,
                             boolean hasError,
                             String errorMessage,
                             String errorStackTrace) {
        AutoCommitJobHistoryRecord record = jobHistoryMap.get(jobId);
        if (record != null) {
            record.addExecution(
                    executionTime,
                    nextExecutionTime,
                    executionNumber,
                    commitsThisExecution,
                    totalCommits,
                    hasError,
                    errorMessage,
                    errorStackTrace);
        }
    }

    public void setJobInactive(String jobId) {
        AutoCommitJobHistoryRecord record = jobHistoryMap.get(jobId);
        if (record != null) {
            record.setActive(false);
        }
    }

    public AutoCommitJobHistoryRecord getJobHistory(String jobId) {
        return jobHistoryMap.get(jobId);
    }

    public List<AutoCommitJobHistoryRecord> getAllJobHistories() {
        return new ArrayList<>(jobHistoryMap.values());
    }

    public void clearJobHistories() {
        jobHistoryMap.clear();
    }
}