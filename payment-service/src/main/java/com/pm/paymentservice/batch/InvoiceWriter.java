package com.pm.paymentservice.batch;


import com.pm.paymentservice.model.Invoice;
import com.pm.paymentservice.repository.InvoiceRepository;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceWriter implements ItemWriter<Invoice> {

    private final InvoiceRepository invoiceRepository;

    public InvoiceWriter(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public void write(Chunk<? extends Invoice> chunk) throws Exception {
        invoiceRepository.bulkCreate((List<Invoice>) chunk.getItems());
    }
}
