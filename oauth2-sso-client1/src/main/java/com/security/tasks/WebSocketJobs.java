package com.security.tasks;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * 实现服务器主动向客户端推送消息
 * <p>
 * SpringBoot封装得太好，webSocket用起来太简单（好处：用起来方便，坏处：你不知道底层实现）
 * <p>
 * 由于代码中涉及到定时任务，这里我们使用spring自带的定时任务功能@EnableScheduling，
 * 定时任务在配置类上添加@EnableScheduling开启对定时任务的支持（亦可以添加在主启动类上，作用是一样的），
 * 在相应的方法上添加@Scheduled声明需要执行的定时任务。
 * 不添加@EnableScheduling注解定时任务是无法生效的。
 */
@Component
@AllArgsConstructor
public class WebSocketJobs {
    private final SimpMessagingTemplate simpMessagingTemplate;
    /**
     * 5秒自动执行一次
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void execute() {
        // 服务端主动向客户端推送定时任务消息
        simpMessagingTemplate.convertAndSend("/topic/schedule/message", "定时任务群发消息");
    }
}
