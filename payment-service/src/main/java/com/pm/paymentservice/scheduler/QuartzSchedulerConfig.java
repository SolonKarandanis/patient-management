package com.pm.paymentservice.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class QuartzSchedulerConfig {

    @Bean
    public JobDetail monthlyInvoiceJobDetail() {
        return JobBuilder.newJob(MonthlyInvoiceQuartzJob.class)
                .withIdentity("monthlyInvoiceJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger monthlyInvoiceJobTrigger(JobDetail monthlyInvoiceJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(monthlyInvoiceJobDetail)
                .withIdentity("monthlyInvoiceTrigger")
                .withSchedule(cronSchedule("0 0 1 1 * ?")) // Run at 1 AM on the first day of every month
                .build();
    }
}
