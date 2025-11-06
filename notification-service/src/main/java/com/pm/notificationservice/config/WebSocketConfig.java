package com.pm.notificationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String[] ALLOWED_ORIGIN_PATTERNS = {
            "http://localhost:4200","http://localhost:3011","http://localhost:8080","http://www.dut.com"
    };

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");

//        config.enableSimpleBroker("/topic");
//        config.setApplicationDestinationPrefixes("/app");
        //topic/cricket
        //topic/orders

        //-> /app/<url>
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
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}

//to connect to changes
// const socket = new SockJS('http://localhost:4010/ws');
//stompClient = Stomp.over(socket);

// stompClient.connect({}, function (frame) {
//    console.log('Connected: ' + frame);  // Log the successful connection
//    stompClient.subscribe('/topic/notifications', function (message) {
//        showNotification(message.body);  // Handle incoming messages
//    });
//}, function (error) {
//    console.error('WebSocket connection error: ' + error);  // Log WebSocket errors
//});

//function sendMessage() {
//    const message = document.getElementById('message').value;
//    if (stompClient && stompClient.connected) {
//        stompClient.send('/app/sendMessage', {}, message);
//        console.log('Message sent:', message);
//    } else {
//        console.error('WebSocket connection is not established.');
//    }
//}

/////app/sendMessage
//@MessageMapping("/sendMessage")
//@SendTo("/topic/notifications")
//public String sendMessage(String message){
//    System.out.println("message : "+message);
//    return message;
//}
