package com.security.service;

import com.security.model.InMessage;
import com.security.model.OutMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendTopicMessage(InMessage message) {
        OutMessage outMessage = new OutMessage();
        BeanUtils.copyProperties(message, outMessage);
        outMessage.setTime(new Date());
        //发送消息
        template.convertAndSend("/topic/game_rank", outMessage);
    }

    /**
     * 发送聊天消息
     *
     * @param message
     */
    public void sendChatMessage(InMessage message) {
        OutMessage outMessage = new OutMessage();
        BeanUtils.copyProperties(message, outMessage);
        outMessage.setTime(new Date());
        outMessage.setContent(message.getFrom()+" 发送："+message.getContent());
        //发送消息
        template.convertAndSend("/chat/single/" + message.getTo(), outMessage);
    }

    /**
     * 推送服务器JVM负载信息
     */
    public void sendServiceInfo() {
        //系统核数
        int processors = Runtime.getRuntime().availableProcessors();
        //空闲内存
        long freeMemory = Runtime.getRuntime().freeMemory();
        //最大内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        String message = "服务器可用处理器：" + processors+"，虚拟机空闲内存大小："+freeMemory+"，最大内存："+maxMemory;
        OutMessage outMessage = new OutMessage();
        outMessage.setTime(new Date());
        outMessage.setContent(message);
        //发送消息
        template.convertAndSend("/topic/service_info", outMessage);
    }
}
