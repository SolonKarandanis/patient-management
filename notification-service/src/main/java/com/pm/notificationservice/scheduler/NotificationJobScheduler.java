package com.pm.notificationservice.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job sendPendingNotificationsJob;

    public NotificationJobScheduler(JobLauncher jobLauncher, Job sendPendingNotificationsJob) {
        this.jobLauncher = jobLauncher;
        this.sendPendingNotificationsJob = sendPendingNotificationsJob;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2:00 AM
    public void scheduleSendPendingNotificationsJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(sendPendingNotificationsJob, jobParameters);
    }
}
