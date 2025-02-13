package com.emir.gitautocommit.service;

import com.emir.gitautocommit.dto.AutoCommitRequestDto;
import com.emir.gitautocommit.dto.AutoCommitResponseDto;
import com.emir.gitautocommit.dto.StatusDto;
import com.emir.gitautocommit.exception.GitOperationException;
import com.emir.gitautocommit.exception.JobAlreadyExistsException;
import com.emir.gitautocommit.exception.JobNotFoundException;
import com.emir.gitautocommit.job.AutoCommitJob;
import com.emir.gitautocommit.record.AutoCommitJobHistoryRecord;
import com.emir.gitautocommit.util.CronTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class AutoCommitManagementService {

    private final QuartzSchedulerService quartzSchedulerService;
    private final JGitCommitService jGitCommitService;
    private final AutoCommitJobHistoryService jobHistoryService;

    private static String activeJobId = null;

    public String getActiveJobId() {
        return activeJobId;
    }

    public AutoCommitResponseDto scheduleAutoCommitJob(AutoCommitRequestDto requestDto) {
        if (activeJobId != null) {
            throw new JobAlreadyExistsException("A job is already active. Please cancel the current job before scheduling a new one.");
        }
        if (!jGitCommitService.loginWithToken(requestDto.getUsername(), requestDto.getToken())) {
            throw new GitOperationException("Failed to authenticate with Git using provided credentials");
        }
        jGitCommitService.testConnection(requestDto.getRepositoryUrl());

        String repoName = extractRepoName(requestDto.getRepositoryUrl());
        String localRepoPath = "repos/" + requestDto.getUsername() + "/" + repoName;
        String jobId = repoName + "-" + requestDto.getBranch() + "-" + UUID.randomUUID().toString().substring(0, 5);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("autoCommitRequestDto", requestDto);
        jobDataMap.put("executionCount", 0);
        jobDataMap.put("totalCommits", 0);
        jobDataMap.put("localRepoPath", localRepoPath);

        try {
            jGitCommitService.clearDirectory(new File(localRepoPath));

            String cronExpression = CronTranslator.generateCronExpression(requestDto.getCommitIntervalDays(), requestDto.getDailyCommitTriggerTime());
            Date nextExecutionTime = quartzSchedulerService.scheduleJob(jobId, AutoCommitJob.class, cronExpression, jobDataMap);
            activeJobId = jobId;
            jobHistoryService.addJobHistory(jobId, requestDto.getBranch(), requestDto.getRepositoryUrl(), cronExpression, requestDto.getCommitCountRange(), nextExecutionTime);
            AutoCommitJobHistoryRecord jobHistory = jobHistoryService.getJobHistory(jobId);
            return new AutoCommitResponseDto(
                    jobHistory.getJobId(),
                    jobHistory.getBranch(),
                    jobHistory.getRepositoryUrl(),
                    jobHistory.getCronExpression(),
                    jobHistory.getCreatedAt(),
                    jobHistory.getNextExecutionTime(),
                    jobHistory.isActive()
            );
        } catch (Exception e) {
            throw new GitOperationException("Failed to schedule auto-commit job", e);
        }
    }


    public void cancelAutoCommitJob() {
        if (activeJobId == null) {
            throw new JobNotFoundException("No active auto-commit job found to cancel.");
        }

        try {
            quartzSchedulerService.deleteJob(activeJobId);
            jobHistoryService.setJobInactive(activeJobId);
            log.info("AutoCommitJob canceled. jobId: {}", activeJobId);
            activeJobId = null;
        } catch (Exception e) {
            throw new GitOperationException("Failed to cancel auto-commit job", e);
        }
    }


    public StatusDto getStatus() {
        if (activeJobId == null)
            return StatusDto.builder().hasActiveJob(false).build();

        AutoCommitJobHistoryRecord jobHistory = jobHistoryService.getJobHistory(activeJobId);

        // Get the last execution record if available
        List<AutoCommitJobHistoryRecord.ExecutionRecord> executions = jobHistory.getExecutions();
        AutoCommitJobHistoryRecord.ExecutionRecord lastExecution = executions != null && !executions.isEmpty()
            ? executions.getLast()
            : null;

        return StatusDto.builder()
                .hasActiveJob(true)
                .jobId(jobHistory.getJobId())
                .branch(jobHistory.getBranch())
                .repositoryUrl(jobHistory.getRepositoryUrl())
                .cronExpression(jobHistory.getCronExpression())
                .commitCountRange(jobHistory.getCommitCountRange())
                .createdAt(jobHistory.getCreatedAt())
                .lastExecutionTime(jobHistory.getLastExecutionTime())
                .nextExecutionTime(jobHistory.getNextExecutionTime())
                .totalExecutions(jobHistory.getTotalExecutions())
                .totalCommits(jobHistory.getTotalCommits())
                .lastExecutionHadError(lastExecution != null && lastExecution.isHasError())
                .lastErrorMessage(lastExecution != null ? lastExecution.getErrorMessage() : null)
                .build();
    }


    // utils
    private String extractRepoName(String repositoryUrl) {
        if (repositoryUrl == null || repositoryUrl.isEmpty()) {
            throw new IllegalArgumentException("Repository URL cannot be null or empty");
        }
        String repo = repositoryUrl.substring(repositoryUrl.lastIndexOf('/') + 1);
        if (repo.endsWith(".git")) {
            repo = repo.substring(0, repo.length() - 4);
        }
        return repo;
    }
}
