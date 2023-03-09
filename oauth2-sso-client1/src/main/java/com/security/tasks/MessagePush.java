package com.security.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@EnableScheduling
public class MessagePush {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;


    @Scheduled(cron = "0/5 * * * * ?")
    public void startAll() {
//        RequestMsg requestMsg = new RequestMsg();
//        requestMsg.setBody(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
//        String jsonString = JSONObject.toJSONString(requestMsg);
//        simpMessagingTemplate.convertAndSend("/topic/all", "推送定时消息");
        log.info("定时发布 广播消息");
        simpMessagingTemplate.convertAndSend("/topic/all","今天10点开项目启动会-所有人");
    }

    @Scheduled(cron = "0/12 * * * * ?")
    public void startUser() {
        log.info("定时发布 点对点消息");
        // 点对点定时，不生效
        simpMessagingTemplate.convertAndSendToUser("123456", "/cmdFinish", "xxx今天去开会");
    }
}
