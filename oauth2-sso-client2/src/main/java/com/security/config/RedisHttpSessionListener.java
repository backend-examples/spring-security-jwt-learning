package com.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;

@Slf4j
@Configuration
public class RedisHttpSessionListener {

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @EventListener
    public void onCreated(SessionCreatedEvent sessionCreatedEvent) {
        log.info("session: " + sessionCreatedEvent.getSessionId() + "已创建，有效期为：" + sessionCreatedEvent.getSession().getMaxInactiveInterval());
    }

    @EventListener
    public void onDeleted(SessionDeletedEvent sessionDeletedEvent) {
        log.info("session: " + sessionDeletedEvent.getSessionId() + "已删除");
    }

    @EventListener
    public void onExpired(SessionExpiredEvent sessionExpiredEvent) {
        log.info("session: " + sessionExpiredEvent.getSessionId() + "已过期");
        log.info("session内容为：" + sessionRepository.findById(sessionExpiredEvent.getSessionId()));
    }

    @EventListener
    public void onDestroyed(SessionDestroyedEvent sessionDestroyedEvent) {
        log.info("session: " + sessionDestroyedEvent.getSessionId() + "已销毁");
    }
}
