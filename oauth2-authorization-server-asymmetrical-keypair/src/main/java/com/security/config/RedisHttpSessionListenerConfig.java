package com.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.session.Session;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;

@Slf4j
@Configuration
public class RedisHttpSessionListenerConfig {

    /**
     * 监听session创建
     */
    @EventListener
    public void onCreated(SessionCreatedEvent event) {
        String sessionId = event.getSessionId();
        // spring-session提供的session
        Session session = event.getSession();
//        System.out.println("创建:" + sessionId+",有效时间："+session.getMaxInactiveInterval());
        log.info("创建:" + sessionId + ",有效时间：" + session.getMaxInactiveInterval());
    }

    /**
     * 监听session删除
     */
    @EventListener
    public void onDeleted(SessionDeletedEvent event) {
        String sessionId = event.getSessionId();
        // spring-session提供的session
        Session session = event.getSession();
//        System.out.println("删除:" + sessionId);
        log.info("删除: " + sessionId);
    }

    /**
     * 监听session过期
     */
    @EventListener
    public void onExpired(SessionExpiredEvent event) {
        String sessionId = event.getSessionId();
        // spring-session提供的session
        Session session = event.getSession();
//        System.out.println("过期:" + sessionId);
        log.info("过期: " + sessionId);
    }

    /**
     * 监听session销毁
     */
    @EventListener
    public void onDestroyed(SessionDestroyedEvent event) {
        String sessionId  = event.getSessionId();

        Session session = event.getSession();

        log.info("销毁: " + sessionId);
    }
}