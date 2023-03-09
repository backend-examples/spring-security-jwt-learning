package com.security.redis.impl;

import com.security.redis.SessionRepository;
import com.security.utils.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionRepositoryImpl implements SessionRepository {

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    public Session getSessionById(String sessionId) {
        String decodeId = Base64Util.decode(sessionId);

        Session session = sessionRepository.findById(decodeId);

        return session;
    }

    @Override
    public void deleteSessionById(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }
}
