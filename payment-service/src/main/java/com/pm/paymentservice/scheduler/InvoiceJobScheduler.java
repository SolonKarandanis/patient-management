package com.pm.paymentservice.scheduler;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InvoiceJobScheduler {

    private final JobOperator jobOperator;
    private final Job monthlyInvoiceJob;

    public InvoiceJobScheduler(JobOperator jobOperator, Job monthlyInvoiceJob) {
        this.jobOperator = jobOperator;
        this.monthlyInvoiceJob = monthlyInvoiceJob;
    }

    // Run at 1 AM on the first day of every month
    @Scheduled(cron = "0 0 1 1 * ?")
    public void runMonthlyInvoiceJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobRestartException, InvalidJobParametersException {
        jobOperator.start(monthlyInvoiceJob, new JobParametersBuilder()
                .addString("jobId", String.valueOf(System.currentTimeMillis()))
                .toJobParameters());
    }
}
