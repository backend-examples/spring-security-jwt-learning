package com.security.service.impl;

import com.security.pojo.User;
import com.security.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = new BCryptPasswordEncoder().encode("123456");

        // 这里返回的用户名和密码即登录时候用到的
        return new User("admin", password);
    }
}