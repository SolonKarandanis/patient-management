package com.pm.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
        ///topic/cricket
        ///topic/orders

        //-> /app/<url>
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:63342")
                .withSockJS();

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
