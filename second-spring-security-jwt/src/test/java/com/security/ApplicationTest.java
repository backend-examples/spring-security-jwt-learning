package com.security;

import com.security.pojo.User;
import com.security.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisUtil redisUtil;


    @Test
    public void testInsert(){
        User user = new User(1, "LiYi", "123");
        redisUtil.set("LiYi", user);
    }

    @Test
    public void captchaImg() throws IOException {

    }
}
