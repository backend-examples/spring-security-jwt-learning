package com.security;

import com.security.utils.EncodeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthorizationServerTest {

    @Test
    public void testEncode() {
        // 获取盐
        String salt = EncodeUtils.getSalt();
        System.out.println("salt:"+ salt); // 8f5ca7b51f2a4e00b666
        // 对密码 666666 使用 盐 加密。
        salt = "8f5ca7b51f2a4e00b666";
        String clientId = EncodeUtils.encode("3096015076@qq.com", salt);
        System.out.println(clientId); //57f896335fd98b87985780ef8b2ed4ee

        // 再判断
        boolean matches = EncodeUtils.matches(salt, "3096015076@qq.com", clientId);
        System.out.println(matches); // true
    }
}
