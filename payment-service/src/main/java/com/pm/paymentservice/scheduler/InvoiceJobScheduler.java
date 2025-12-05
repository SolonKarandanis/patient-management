package com.pm.paymentservice.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InvoiceJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyInvoiceJob;

    public InvoiceJobScheduler(JobLauncher jobLauncher, Job monthlyInvoiceJob) {
        this.jobLauncher = jobLauncher;
        this.monthlyInvoiceJob = monthlyInvoiceJob;
    }

    // Run at 1 AM on the first day of every month
    @Scheduled(cron = "0 0 1 1 * ?")
    public void runMonthlyInvoiceJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(monthlyInvoiceJob, new JobParametersBuilder()
                .addString("jobId", String.valueOf(System.currentTimeMillis()))
                .toJobParameters());
    }
}
