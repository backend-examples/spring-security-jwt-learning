package com.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅消息的前缀：topic用来广播，user用来实现点对点
        registry.enableSimpleBroker("/topic", "/queue");
        // 点对点发送前缀
        registry.setUserDestinationPrefix("/queue");
        //设置消息发布订阅的头(客户端发送数据给服务器的一个前缀)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP的endpoint，前端建立socket连接时url(http://127.0.0.1:8080/api)
        registry.addEndpoint("/api")
                // 新版本允许跨域
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
