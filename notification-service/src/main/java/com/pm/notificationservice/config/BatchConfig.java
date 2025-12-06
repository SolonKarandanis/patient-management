package com.pm.notificationservice.config;

import com.pm.notificationservice.model.NotificationEventEntity;
import com.pm.notificationservice.model.NotificationEventStatus;
import com.pm.notificationservice.repository.NotificationEventRepository;
import com.pm.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NotificationEventRepository notificationEventRepository;
    private final NotificationService notificationService;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       NotificationEventRepository notificationEventRepository,
                       NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.notificationEventRepository = notificationEventRepository;
        this.notificationService = notificationService;
    }

    @Bean
    public Job sendPendingNotificationsJob(Step sendNotificationsStep) {
        return new JobBuilder("sendPendingNotificationsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sendNotificationsStep)
                .build();
    }

    @Bean
    public Step sendNotificationsStep(ItemReader<NotificationEventEntity> reader,
                                      ItemProcessor<NotificationEventEntity, NotificationEventEntity> processor,
                                      ItemWriter<NotificationEventEntity> writer) {
        return new StepBuilder("sendNotificationsStep", jobRepository)
                .<NotificationEventEntity, NotificationEventEntity>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public RepositoryItemReader<NotificationEventEntity> notificationEventReader() {
        return new RepositoryItemReaderBuilder<NotificationEventEntity>()
                .name("notificationEventReader")
                .repository(notificationEventRepository)
                .methodName("findByStatus")
                .arguments(NotificationEventStatus.NOTIFICATION_EVENT_PENDING)
                .pageSize(10)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<NotificationEventEntity, NotificationEventEntity> notificationEventProcessor() {
        return item -> {
            try {
                // Send notification using existing service
                notificationService.sendNotification(item);
                // Update status and sentDate on the item (will be persisted by the writer)
                item.setStatus(NotificationEventStatus.NOTIFICATION_EVENT_SENT);
                item.setSentDate(LocalDateTime.now());

            } catch (Exception e) {
                // Log error and set status to failed on the item (will be persisted by the writer)
                item.setStatus(NotificationEventStatus.NOTIFICATION_EVENT_FAILED);
                log.error("Failed to send notification for ID: {}, error: {}",  item.getId(), e.getMessage());
            }
            return item; // Return the updated entity
        };
    }
    @Bean
    public ItemWriter<NotificationEventEntity> notificationEventWriter() {
        return notificationEventRepository::saveAll;
    }
}
