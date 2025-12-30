package com.pm.paymentservice.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("monthlyInvoiceQuartzJob")
public class MonthlyInvoiceQuartzJob implements Job {

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private org.springframework.batch.core.job.Job monthlyInvoiceJob;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            jobOperator.start(monthlyInvoiceJob, new JobParametersBuilder()
                    .addString("jobId", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters());
        } catch (Exception e) {
            throw new JobExecutionException("Failed to run monthly invoice job", e);
        }
    }
}
