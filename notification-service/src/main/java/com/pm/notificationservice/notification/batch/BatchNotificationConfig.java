package com.pm.notificationservice.notification.batch;

import com.pm.notificationservice.notification.model.NotificationEventEntity;
import com.pm.notificationservice.notification.model.NotificationEventStatus;
import com.pm.notificationservice.notification.repository.NotificationEventRepository;
import com.pm.notificationservice.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
@Slf4j
public class BatchNotificationConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NotificationEventRepository notificationEventRepository;
    private final NotificationService notificationService;

    public BatchNotificationConfig(JobRepository jobRepository,
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
    public Step sendNotificationsStep(@Qualifier("notificationEventReader") ItemReader<NotificationEventEntity> reader,
                                      @Qualifier("notificationEventProcessor") ItemProcessor<NotificationEventEntity, NotificationEventEntity> processor,
                                      @Qualifier("notificationEventWriter") ItemWriter<NotificationEventEntity> writer) {
        return new StepBuilder("sendNotificationsStep", jobRepository)
                .<NotificationEventEntity, NotificationEventEntity>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean(name = "notificationEventReader")
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

    @Bean(name = "notificationEventProcessor")
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

    @Bean(name = "asyncNotificationEventProcessor")
    public AsyncItemProcessor<NotificationEventEntity, NotificationEventEntity> asyncNotificationEventProcessor(){
        AsyncItemProcessor<NotificationEventEntity, NotificationEventEntity> processor = new AsyncItemProcessor<>(notificationEventProcessor());
        processor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return processor;
    }


    @Bean(name = "notificationEventWriter")
    public ItemWriter<NotificationEventEntity> notificationEventWriter() {
        return notificationEventRepository::saveAll;
    }

    @Bean(name = "asyncNotificationEventWriter")
    public AsyncItemWriter<NotificationEventEntity> asyncNotificationEventWriter() {
        AsyncItemWriter<NotificationEventEntity> writer = new AsyncItemWriter<>(notificationEventWriter());
        return writer;
    }
}
