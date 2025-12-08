package com.pm.analyticsservice.config;

import com.pm.analyticsservice.model.PatientEventModel;
import com.pm.analyticsservice.service.KafkaNotificationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notification.events.NotificationEvent;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MonthlyPatientReportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final KafkaNotificationGateway kafkaNotificationGateway;

    @Bean
    public ItemReader<PatientEventModel> monthlyPatientReportReader() {
        JdbcCursorItemReader<PatientEventModel> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, patientId, name, email, event_type, event_timestamp FROM analyticsservice.patient_events WHERE event_timestamp >= today() - 30");
        reader.setRowMapper(new BeanPropertyRowMapper<>(PatientEventModel.class));
        return reader;
    }

    @Bean
    public ItemWriter<PatientEventModel> monthlyPatientReportWriter() {
        // For this job, we just log the processing. The real output is the notification.
        return items -> {
            log.info("Processing  {}  patient events..." ,items.size());
        };
    }

    @Bean
    public JobCompletionNotificationListener jobCompletionListener() {
        return new JobCompletionNotificationListener(kafkaNotificationGateway);
    }

    @Bean
    public Step monthlyReportStep() {
        return new StepBuilder("monthlyReportStep", jobRepository)
                .<PatientEventModel, PatientEventModel>chunk(100, transactionManager)
                .reader(monthlyPatientReportReader())
                .writer(monthlyPatientReportWriter())
                .build();
    }

    @Bean
    public Job monthlyReportJob(JobCompletionNotificationListener listener) {
        return new JobBuilder("monthlyReportJob", jobRepository)
                .start(monthlyReportStep())
                .listener(listener)
                .build();
    }

    public static class JobCompletionNotificationListener implements JobExecutionListener {

        private final KafkaNotificationGateway kafkaNotificationGateway;
        private long recordCount = 0;

        public JobCompletionNotificationListener(KafkaNotificationGateway kafkaNotificationGateway) {
            this.kafkaNotificationGateway = kafkaNotificationGateway;
        }

        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("Monthly report job started.");
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            jobExecution.getStepExecutions().forEach(stepExecution -> recordCount += stepExecution.getWriteCount());

            String statusType;
            String message;

            if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                statusType = "MONTHLY_REPORT_JOB_COMPLETED_SUCCESS";
                message = "Monthly patient report job completed successfully. Processed " + recordCount + " records.";
            } else {
                statusType = "MONTHLY_REPORT_JOB_COMPLETED_FAILED";
                message = "Monthly patient report job failed. Status: " + jobExecution.getExitStatus().getExitCode();
            }

            NotificationEvent notification =
                    NotificationEvent.newBuilder()
                            .addUserIds("admin-dashboard") //needs user ids
                            .setTitle("Monthly Patient Report Job")
                            .setEventType(statusType)
                            .setMessage(message)
                            .build();

            kafkaNotificationGateway.sendNotification(notification);
            log.info("Job completion notification sent to Kafka.");
        }
    }
}
