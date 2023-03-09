package com.security.controller;

import com.security.model.ChatMessage;
import com.security.service.InstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instant")
public class InstantController {

    @Autowired
    private InstantService instantService;

    /**
     * 客户端新增用户消息入口，用于群发显示：新进入xx用户
     * @param chatMessage
     * @param headerAccessor
     * @return
     */
    @MessageMapping("/chat/addUser")
    @SendTo({"/topic/public"})
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    /**
     * 一对一消息发送
     * @param chatMessage
     */
    @PostMapping("/chat/single")
    public void singleMessage(@RequestBody ChatMessage chatMessage) {
        instantService.sendSingleMessage(chatMessage);
    }

    /**
     * 群聊
     */
    @PostMapping("/chat/group")
    public void groupMessage(@RequestBody ChatMessage chatMessage) {
        instantService.sendGroupMessage(chatMessage);
    }
}
