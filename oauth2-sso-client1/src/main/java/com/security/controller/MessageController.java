package com.security.controller;

import com.alibaba.fastjson.JSONObject;
import com.security.utils.ResponseResult;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
/**
 * 服务器消息处理器
 * <p>
 * 实现步骤：
 * 1、创建WebSocket的控制层类，并注入用于发送消息的SimpMessagingTemplate。
 * 2、配置通过@MessageMapping注解修饰的方法来接收客户端SEND的操作。
 * 3、配置通过@SubscribeMapping注解修饰的方法来接收客户端SUBSCRIBE的操作。
 * 4、配置通过@SendTo注解的方法来直接将消息推送的指定地址上。
 */
@Controller
@AllArgsConstructor
@RequestMapping("/message")
// @MessageMapping("/message")
public class MessageController {
    /**
     * SimpMessagingTemplate：是Spring-WebSocket内置的一个消息发送工具，可以将服务端消息主动推送到指定的客户端。
     * spring websocket基于注解的@SendTo和@SendToUser虽然方便，但是有局限性，例如我这样子的需求，
     * 我想手动的把消息推送给某个人，或者特定一组人，怎么办，@SendTo只能推送给所有人，@SendToUser只能
     * 推送给请求消息的那个人，这时，我们可以利用SimpMessagingTemplate这个类。
     * <p>
     * SimpMessagingTemplate有俩个推送的方法：
     * convertAndSend(destination, payload); //将消息广播到特定订阅路径中，类似@SendTo
     * convertAndSendToUser(user, destination, payload);//将消息推送到固定的用户订阅路径中，类似@SendToUser
     */
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{userId}")
    public String page(@PathVariable("userId") long userId, HttpServletRequest request) {
        request.setAttribute("userId", userId);
        return "message";
    }

    /**
     * 订阅消息
     * 只是在订阅的时候触发，可以理解为：访问 ——> 返回数据
     * 这里的消息会在订阅的时候自动发送到订阅的客户端
     *
     * @param userId
     * @return
     */
    @SubscribeMapping("/subscribe/{userId}")
    public String subscribe(@DestinationVariable long userId) {
        return "恭喜你，订阅成功!!! 欢迎ID为" + userId + "的用户！";
    }
    /**
     * 广播消息、群发消息
     * （1）MessageMapping注解：Spring对于WebSocket封装的特别简单，提供了一个@MessageMapping注解，
     * 功能类似@RequestMapping，它是存在于Controller中的，定义一个消息的基本请求，功能也
     * 跟@RequestMapping类似，包括支持通配符``的url定义等等，详细用法参见Annotation Message Handling
     * （2）SendTo注解：定义了消息的目的地。可以将请求消息转发到订阅了/topic/broadcast消息的客户端。
     * /topic/broadcast是客户端和服务端建立websocket连接后，客户端订阅服务端消息的地址，用于监听接收服务端的实时消息。
     * <p>
     * 服务端接收客户端发送的消息，类似OnMessage方法
     *
     * @param message
     * @return
     */
    @MessageMapping("/broadcast/string")
    @SendTo("/topic/broadcast")
    public String broadcastString(String message) {
        // 请求是String普通文本
        System.out.println("接收到客户端发来的广播消息：" + HtmlUtils.htmlEscape(message));
        // 接收到客户端消息后，将消息发送到订阅了/topic/broadcast的客户端
        // 如果不加@SendTo("/topic/broadcast")，则不会发送消息给客户端，即使这里有返回消息内容也不会发送消息给客户端
        // 返回String普通文本
        return message;
    }
    @MessageMapping("/broadcast/json")
    @SendTo("/topic/broadcast")
    public ResponseResult broadcastJson(String message) {
        // 请求参数是json对象
//        RequestMessage requestMessage = JSONObject.parseObject(message, RequestMessage.class);
//        System.out.println("接收到客户端发来的广播消息：" + requestMessage.getContent());
        ResponseResult responseResult = JSONObject.parseObject(message, ResponseResult.class);
        // 返回数据是json对象
        return responseResult;
    }
    @MessageMapping("/broadcast/noreturn")
    public void broadcastNoreturn(String message) {
        // 请求是String普通文本
        System.out.println("接收到客户端发来的广播消息：" + HtmlUtils.htmlEscape(message));
        // 接收到客户端消息后，将消息发送到订阅了/topic/broadcast的客户端
        // 如果不加@SendTo("/topic/broadcast")，有没有返回值都不会发送消息给客户端
        // 此时可以使用simpMessagingTemplate.convertAndSend方法发送消息给客户端
        simpMessagingTemplate.convertAndSend("/topic/broadcast", HtmlUtils.htmlEscape(message));
    }
    /**
     * 点对点式消息、队列消息
     * 注意为什么使用queue，主要目的是为了区分广播和队列的方式。
     * 实际采用topic，也没有关系。但是为了好理解。
     * 如果存在return,可以使用注解：@SendToUser
     * 也可以直接使用：simpMessagingTemplate.convertAndSendToUser方法
     *
     * @param message
     * @return
     */
    @MessageMapping("/user/send/{userId}")
    @SendToUser("/notice")
    // 这个接口没有调通，目前无法成功发送消息给客户端，因为我没有实现客户端认证功能，这里就不测试了
    // 一般需要整合认证框架类似spring security才能使用@SendToUser注解
    public String userSend(String message, Principal principal) {
        System.out.println("接收到客户端发来的用户消息：" + HtmlUtils.htmlEscape(message));
        System.out.println("用户：" + principal.getName());
        // 消息中转站，消息中转处理
        // 使用@SendToUser注解需要客户端登录才能接收到服务端消息，一般整合spring security来实现认证，接收用户为principal.getName()
        // 由于@SendToUser注解需要客户端登录认证，如果不想要客户端登录认证，推荐使用simpMessagingTemplate.convertAndSendToUser来发送消息给用户
        return HtmlUtils.htmlEscape(message);
    }
    /**
     * 推荐使用simpMessagingTemplate.convertAndSendToUser发送点对点消息
     *
     * @param userId
     * @param message
     */
    @MessageMapping("/user/notice/{userId}")
    public void userNotice(@DestinationVariable Long userId, String message) {
        System.out.println("接收到客户端发来的用户消息：" + HtmlUtils.htmlEscape(message));
        // 此时可以使用simpMessagingTemplate.convertAndSendToUser方法发送消息给客户端
        // 这种方式比较灵活，可以不要求客户端登录即可发送消息，不推荐使用@SendToUser注解
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/notice", HtmlUtils.htmlEscape(message));
    }
    @ResponseBody
    @GetMapping("/push/notice/{userId}")
    public String pushNotice(@PathVariable("userId") Long userId, @RequestParam String message) {
        System.out.println("接收到客户端发来的队列消息：" + HtmlUtils.htmlEscape(message));
        // 此时可以使用simpMessagingTemplate.convertAndSendToUser方法发送消息给客户端
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/notice", HtmlUtils.htmlEscape(message));
        return "SUCCESS";
    }
    @ResponseBody
    @PostMapping("/push/{userId}")
    public String push(@PathVariable("userId") Long userId, @RequestParam String message) {
        simpMessagingTemplate.convertAndSend("/queue/message/" + userId, message);
        return "SUCCESS";
    }
    @ResponseBody
    @PostMapping("/pushAll")
    public String pushAll(@RequestParam String message) {
        simpMessagingTemplate.convertAndSend("/topic/message", message);
        return "SUCCESS";
    }
}
