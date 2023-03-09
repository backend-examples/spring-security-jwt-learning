package com.security.service.impl;

import com.security.model.ChatMessage;
import com.security.service.InstantService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InstantServiceImpl implements InstantService {

    @Resource
    private SimpMessagingTemplate simpleMessageTemplate;

    @Override
    public ChatMessage sendSingleMessage(ChatMessage chatMessage) {
        simpleMessageTemplate.convertAndSendToUser(chatMessage.getToUser(), "/single", chatMessage);

        return chatMessage;
    }

    @Override
    public ChatMessage sendGroupMessage(ChatMessage chatMessage) {
        simpleMessageTemplate.convertAndSend("/group", chatMessage);

        return chatMessage;
    }
}
