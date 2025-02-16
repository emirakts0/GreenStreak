package com.emir.gitautocommit.job;

import com.emir.gitautocommit.config.ApplicationContextProvider;
import com.emir.gitautocommit.dto.AutoCommitRequestDto;
import com.emir.gitautocommit.service.JGitCommitService;
import com.emir.gitautocommit.service.AutoCommitJobHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import java.util.Date;
import java.util.Random;


@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoCommitJob implements Job {

    private static final Random random = new Random();

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        AutoCommitRequestDto autoCommitRequestDto = (AutoCommitRequestDto) dataMap.get("autoCommitRequestDto");
        String localRepoPath = dataMap.getString("localRepoPath");
        int executionCount = dataMap.getInt("executionCount");
        int totalCommits = dataMap.getInt("totalCommits");
        int commitCount = 0;
        boolean hasError = false;
        String errorMessage = null;
        String errorStackTrace = null;

        log.info("Starting scheduled commit job - Repository: {}, Branch: {}", autoCommitRequestDto.getRepositoryUrl(), autoCommitRequestDto.getBranch());
        log.debug("Job parameters - LocalPath: {}, CommitRange: {}, ExecutionCount: {}, TotalCommits: {}", 
                 localRepoPath, autoCommitRequestDto.getCommitCountRange(), executionCount, totalCommits);

        try {
            JGitCommitService commitService = ApplicationContextProvider.getApplicationContext().getBean(JGitCommitService.class);
            commitCount = parseCommitCount(autoCommitRequestDto.getCommitCountRange());
            log.debug("Selected commit count for this execution: {} (from range: {})", commitCount, autoCommitRequestDto.getCommitCountRange());
            
            commitService.makeNCommits(commitCount, localRepoPath, autoCommitRequestDto.getBranch(), autoCommitRequestDto.getRepositoryUrl(), autoCommitRequestDto.getUsername(), autoCommitRequestDto.getEmail());
            executionCount++;
            totalCommits += commitCount;
            log.info("Commit job execution successful - Created {} commits", commitCount);
            log.debug("Updated statistics - Total executions: {}, Total commits: {}", executionCount, totalCommits);
        } catch (Exception e) {
            hasError = true;
            errorMessage = e.getMessage();
            errorStackTrace = ExceptionUtils.getStackTrace(e);
            log.error("Commit job execution failed: {}", errorMessage, e);
        }

        updateJobDataMap(dataMap, executionCount, totalCommits);
        updateJobHistory(context, commitCount, hasError, errorMessage, errorStackTrace);
    }

    private void updateJobDataMap(JobDataMap dataMap, int executionCount, int totalCommits) {
        log.debug("Updating job data map - ExecutionCount: {}, TotalCommits: {}", executionCount, totalCommits);
        dataMap.put("executionCount", executionCount);
        dataMap.put("totalCommits", totalCommits);
    }

    private void updateJobHistory(JobExecutionContext context, int commitCount, 
                                boolean hasError, String errorMessage, String errorStackTrace) {
        try {
            AutoCommitJobHistoryService historyService = ApplicationContextProvider.getApplicationContext()
                    .getBean(AutoCommitJobHistoryService.class);
            String jobId = context.getJobDetail().getKey().getName();
            Date currentExecutionTime = new Date();
            Date nextExecutionTime = getNextFireTime(context);

            log.debug("Updating job history - JobId: {}, CommitCount: {}, HasError: {}", 
                     jobId, commitCount, hasError);
            
            historyService.addExecution(
                    jobId,
                    currentExecutionTime,
                    nextExecutionTime,
                    context.getJobDetail().getJobDataMap().getInt("executionCount"),
                    commitCount,
                    context.getJobDetail().getJobDataMap().getInt("totalCommits"),
                    hasError,
                    errorMessage,
                    errorStackTrace
            );
        } catch (Exception e) {
            log.error("Failed to update job history: {}", e.getMessage(), e);
        }
    }

    private Date getNextFireTime(JobExecutionContext context) {
        Trigger trigger = context.getTrigger();
        return trigger.getNextFireTime();
    }

    private int parseCommitCount(String range) {
        if (range.contains("-")) {
            String[] parts = range.split("-");
            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());
            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
            return random.nextInt(max - min + 1) + min;
        } else {
            return Integer.parseInt(range.trim());
        }
    }
}
