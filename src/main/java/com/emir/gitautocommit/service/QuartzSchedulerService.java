package com.emir.gitautocommit.service;

import com.emir.gitautocommit.exception.GitAutoCommitException;
import com.emir.gitautocommit.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuartzSchedulerService {

    private final Scheduler scheduler;

    public Date scheduleJob(String jobId, Class<? extends Job> jobClass, String cronExpression) {
        return scheduleJob(jobId, jobClass, cronExpression, new JobDataMap());
    }

    public Date scheduleJob(String jobId, Class<? extends Job> jobClass, String cronExpression, JobDataMap jobDataMap) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobId, "DEFAULT")
                    .usingJobData(jobDataMap)
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobId + "Trigger", "DEFAULT")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Job {} scheduled with cron: {}  next fire time: {}", jobId, cronExpression, trigger.getNextFireTime());
            return trigger.getNextFireTime();
        } catch (SchedulerException e) {
            throw new GitAutoCommitException("Failed to schedule job: " + jobId, e);
        }
    }


    public void deleteJob(String jobId) {
        try {
            JobKey jobKey = new JobKey(jobId, "DEFAULT");
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobId);
            }
            
            boolean deleted = scheduler.deleteJob(jobKey);
            if (deleted) {
                log.info("Job {} deleted successfully", jobId);
            } else {
                throw new GitAutoCommitException("Failed to delete job: " + jobId);
            }
        } catch (SchedulerException e) {
            throw new GitAutoCommitException("Error while deleting job: " + jobId, e);
        }
    }


    public boolean isJobExists(String jobId) {
        try {
            JobKey jobKey = new JobKey(jobId, "DEFAULT");
            return scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            throw new GitAutoCommitException("Error checking job existence: " + jobId, e);
        }
    }
}