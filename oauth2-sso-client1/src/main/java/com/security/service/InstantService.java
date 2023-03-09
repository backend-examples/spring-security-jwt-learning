package com.security.service;

import com.security.model.ChatMessage;

public interface InstantService {

    ChatMessage sendSingleMessage(ChatMessage chatMessage);

    ChatMessage sendGroupMessage(ChatMessage chatMessage);
}
