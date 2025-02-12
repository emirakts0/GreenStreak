package com.emir.gitautocommit.util;

import com.emir.gitautocommit.exception.GitAutoCommitException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CronTranslator {

    /**
     * Generates a cron expression for the given interval days and daily trigger time.
     * For example:
     * - intervalDays=1, dailyTriggerTime="14:30" -> "0 30 14 * * ?" (every day at 14:30)
     * - intervalDays=2, dailyTriggerTime="10:00" -> "0 0 10 1/2 * ?" (every 2 days at 10:00)
     * - intervalDays=7, dailyTriggerTime="09:15" -> "0 15 9 1/7 * ?" (every 7 days at 09:15)
     */
    public static String generateCronExpression(int intervalDays, String dailyTriggerTime) {
        log.debug("Generating cron expression - intervalDays: {} dailyTriggerTime: {}", intervalDays, dailyTriggerTime);
        
        if (dailyTriggerTime == null || dailyTriggerTime.isBlank()) {
            throw new IllegalArgumentException("Daily trigger time cannot be null or empty");
        }

        try {
            String[] timeParts = dailyTriggerTime.split(":");
            if (timeParts.length != 2) {
                throw new IllegalArgumentException("Invalid time format. Expected HH:mm format");
            }

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            validateTimeParameters(intervalDays, hour, minute);

            // Format: "0 <minute> <hour> 1/<intervalDays> * ?"
            // Using '?' for day-of-week to avoid conflicts with day-of-month
            String cronExpression = String.format("0 %d %d 1/%d * ?", minute, hour, intervalDays);
            log.debug("Generated cron expression: {}", cronExpression);
            return cronExpression;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time format. Time parts must be valid integers", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new GitAutoCommitException("Failed to generate cron expression", e);
        }
    }

    private static void validateTimeParameters(int intervalDays, int hour, int minute) {
        if (intervalDays <= 0) {
            throw new IllegalArgumentException("Interval days must be greater than 0");
        }
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Minute must be between 0 and 59");
        }
    }
}
