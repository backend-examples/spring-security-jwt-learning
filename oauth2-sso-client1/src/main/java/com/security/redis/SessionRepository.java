package com.security.redis;

import org.springframework.session.Session;

public interface SessionRepository {

    Session getSessionById(String sessionId);

    void deleteSessionById(String sessionId);
}
