package com.security.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * 订阅事件监听器
 * @author: chenyanbin 2022-07-03 17:35
 */
@Slf4j
@Component
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        //消息头响应器
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        //消息类型
        SimpMessageType messageType = headerAccessor.getCommand().getMessageType();

        log.warn("【SubscribeEventListener监听器事件】消息类型：" + messageType);
    }
}
