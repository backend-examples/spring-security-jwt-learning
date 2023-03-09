package com.security.controller;

import com.security.utils.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/communication")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommunicationController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/sendMsg")
    public ResponseResult<String> sendMsg(){
        simpMessagingTemplate.convertAndSend("/topic/all","今天10点开项目启动会-所有人");
        return ResponseResult.builder().success();
    }

    @PostMapping("/single")
    public ResponseResult<String> single() {
        simpMessagingTemplate.convertAndSendToUser("111111","/queue/cmdFinish","今天10点开项目启动会-linxin");
        return ResponseResult.builder().success();
    }
}
