package com.pm.streamprocessor.service;

import com.pm.streamprocessor.model.AggregateTotal;
import com.pm.streamprocessor.model.AggregateTotalSerdes;
import com.pm.streamprocessor.model.payment.PaymentDataModel;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

@Component
public class PaymentsKTableProcessor {

    //KTABLE STATE: Create a KTable for State of payments per patient
    public void process(KStream<String, PaymentDataModel> stream){
        //Create a new KeyValue Store
        KeyValueBytesStoreSupplier patientPayments = Stores.persistentKeyValueStore(
                "patient-payments-amount");

        KGroupedStream<String, Double> paymentsByPatientId = stream
                .map((key, payment) -> new KeyValue(payment.getPatientId(), payment.getAmount()))
                .groupByKey();

        KTable<String, AggregateTotal> paymentsAggregate = paymentsByPatientId.aggregate(AggregateTotal::new,
                (k,v,aggregate) -> {
                    aggregate.setCount(aggregate.getCount()+1);
                    aggregate.setAmount(aggregate.getAmount()+v);
                    return aggregate;
                }, Materialized.with(Serdes.String(),new AggregateTotalSerdes()));

        final KTable<String, Double> dealerTotal =
                paymentsAggregate.mapValues(AggregateTotal::getAmount,Materialized.as(patientPayments));
    }
}
