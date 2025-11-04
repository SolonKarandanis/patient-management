package com.pm.streamprocessor.config;

import com.pm.streamprocessor.model.payment.PaymentDataModel;
import com.pm.streamprocessor.model.payment.PaymentDataModelSerde;
import com.pm.streamprocessor.service.LatestMonthPaymentsKStreamProcessor;
import com.pm.streamprocessor.service.PaymentsKTableProcessor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class KafkaStreamsConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${payment.processing.topic-name}")
    private String paymentsInputTopic;

    @Autowired
    private LatestMonthPaymentsKStreamProcessor latestMonthPaymentsKstreamProcessor;

    @Autowired
    private PaymentsKTableProcessor paymentsKtableProcessor;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Double().getClass().getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "payments");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public KStream<String, PaymentDataModel> kStream(StreamsBuilder kStreamBuilder) {

        KStream<String, PaymentDataModel> stream = kStreamBuilder.stream(paymentsInputTopic, Consumed.with(Serdes.String(), new PaymentDataModelSerde()));
        //Process KStream
        this.latestMonthPaymentsKstreamProcessor.process(stream);

        //Process KTable
        this.paymentsKtableProcessor.process(stream);

        return stream;
    }
}


//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StoreQueryParameters;
//import org.apache.kafka.streams.state.QueryableStoreTypes;
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
//import org.springframework.kafka.config.StreamsBuilderFactoryBean;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/toyotasales")
//public class StreamsController {
//
//    private final StreamsBuilderFactoryBean factoryBean;
//
//    public StreamsController(StreamsBuilderFactoryBean factoryBean) {
//        this.factoryBean=factoryBean;
//    }
//
//    /** READ FROM THE KTABLE DEALER STATE **/
//    @GetMapping("/dealer/{id}")
//    public String getDealerSales(@PathVariable String id){
//        KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
//        //Read the KTable Store and get the total aggregated sales by dealer id
//        ReadOnlyKeyValueStore<String, Long> amounts = kafkaStreams
//                .store(StoreQueryParameters.fromNameAndType("dealer-sales-amount", QueryableStoreTypes.keyValueStore()));
//        return "Total Car Sales for Dealer "+id +" is $"+ amounts.get(id);
//    }
//
//}
