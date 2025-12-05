package com.pm.paymentservice.config;

import com.pm.paymentservice.batch.InvoiceProcessor;
import com.pm.paymentservice.batch.InvoiceWriter;
import com.pm.paymentservice.batch.PatientIdReader;
import com.pm.paymentservice.model.Invoice;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    private final PatientIdReader patientIdReader;
    private final InvoiceProcessor invoiceProcessor;
    private final InvoiceWriter invoiceWriter;

    public BatchConfig(PatientIdReader patientIdReader, InvoiceProcessor invoiceProcessor, InvoiceWriter invoiceWriter) {
        this.patientIdReader = patientIdReader;
        this.invoiceProcessor = invoiceProcessor;
        this.invoiceWriter = invoiceWriter;
    }

    @Bean
    public Step generateInvoicesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("generateInvoicesStep", jobRepository)
                .<Long, Invoice>chunk(10, transactionManager)
                .reader(patientIdReader)
                .processor(invoiceProcessor)
                .writer(invoiceWriter)
                .build();
    }

    @Bean
    public Job monthlyInvoiceJob(JobRepository jobRepository, Step generateInvoicesStep) {
        return new JobBuilder("monthlyInvoiceJob", jobRepository)
                .start(generateInvoicesStep)
                .build();
    }
}
