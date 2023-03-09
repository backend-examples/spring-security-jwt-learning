package com.security.model;

import lombok.Data;

@Data
public class ChatMessage {

    /**
     * @Feild 消息类型
     */
    private MessageType type;

    /**
     * @Feild 消息内容
     */
    private String content;

    /**
     * @Feild 消息发送者
     */
    private String sender;

    /**
     * @Feild 消息接收者
     */
    private String toUser;

    /**
     * @Feild 消息枚举类
     */
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
