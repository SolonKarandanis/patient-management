package com.pm.notificationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import reactor.netty.tcp.TcpClient;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Value("${artemis.stomp.relay.host}")
    private String hosts;

    @Value("${artemis.stomp.relay.port}")
    private Integer port;

    @Value("${spring.artemis.user}")
    private String user;

    @Value("${spring.artemis.password}")
    private String password;



    public static final String[] ALLOWED_ORIGIN_PATTERNS = {
            "http://localhost:4200","http://localhost:3011","http://localhost:8080","http://www.dut.com"
    };

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        List<InetSocketAddress> addresses = Arrays.stream(hosts.split(","))
                .map(String::trim)
                .map(h -> new InetSocketAddress(h, port))
                .toList();
        log.info("-------> Initializing WebSocketConfig");
        log.info("-------> STOMP brokers: {}", addresses);

        AtomicInteger idx = new AtomicInteger(0);

        TcpClient tcpClient = TcpClient.create()
                .remoteAddress(() -> {
                    InetSocketAddress addr = addresses.get(Math.abs(idx.getAndIncrement()) % addresses.size());
                    log.info("-------> STOMP relay connecting to {}", addr);
                    return addr;
                });

        ReactorNettyTcpClient<byte[]> failoverClient =
                new ReactorNettyTcpClient<>(tcpClient, new StompReactorNettyCodec());

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        taskScheduler.initialize();

        registry.setApplicationDestinationPrefixes("/app");
        registry.enableStompBrokerRelay("/topic")
                .setTcpClient(failoverClient)
                .setClientLogin(user)
                .setClientPasscode(password)
                .setSystemLogin(user)
                .setSystemPasscode(password)
                .setTaskScheduler(taskScheduler);

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(ALLOWED_ORIGIN_PATTERNS)
                .withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}
