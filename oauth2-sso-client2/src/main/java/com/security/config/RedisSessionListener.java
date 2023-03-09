package com.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;

@Slf4j
@Configuration
public class RedisSessionListener {

    @EventListener
    public void onCreated(SessionCreatedEvent sessionCreatedEvent) {
        log.info("session " + sessionCreatedEvent.getSessionId() + "已创建, 有效期为：" + sessionCreatedEvent.getSession().getMaxInactiveInterval());
    }

    /**
     * 监听session删除
     */
    @EventListener
    public void onDeleted(SessionDeletedEvent sessionDeletedEvent) {
        log.info("session: " + sessionDeletedEvent.getSessionId() + "已删除");
    }

    /**
     * 监听session过期
     */
    @EventListener
    public void onExpired(SessionExpiredEvent sessionExpiredEvent) {
        log.info("session: " + sessionExpiredEvent.getSessionId() + "已过期");
    }

    @EventListener
    public void onDestroyed(SessionDestroyedEvent sessionDestroyedEvent) {
        log.info("session " + sessionDestroyedEvent.getSessionId() + "已销毁");
    }
}
