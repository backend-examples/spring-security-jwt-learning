package com.security.controller;

import com.security.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class V4ServiceInfoController {
    @Autowired
    private WebSocketService webSocketService;

    @MessageMapping("/v4/schedule/push")
    //3秒一次
    @Scheduled(fixedDelay = 3000)
    public void sendServiceInfo(){
//        webSocketService.sendServiceInfo();
    }
}
