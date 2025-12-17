package com.pm.notificationservice.scheduler;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationJobScheduler {

    private final JobOperator jobOperator;
    private final Job sendPendingNotificationsJob;

    public NotificationJobScheduler(JobOperator jobOperator, Job sendPendingNotificationsJob) {
        this.jobOperator = jobOperator;
        this.sendPendingNotificationsJob = sendPendingNotificationsJob;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2:00 AM
    public void scheduleSendPendingNotificationsJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobOperator.start(sendPendingNotificationsJob, jobParameters);
    }
}
