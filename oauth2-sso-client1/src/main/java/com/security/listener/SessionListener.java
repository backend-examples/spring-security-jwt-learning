package com.security.listener;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Slf4j
@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
      log.info("session " + se.getSession().getId() + "已创建, 有效期为：" + se.getSession().getMaxInactiveInterval());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("session " + se.getSession().getId() + "已销毁");
    }
}
