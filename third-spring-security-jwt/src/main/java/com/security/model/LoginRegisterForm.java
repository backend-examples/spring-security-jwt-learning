package com.security.model;

import com.security.pojo.User;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class LoginRegisterForm {

    private String username;

    private String password;

    private Integer roleId;

    @SneakyThrows
    public User toUser(LoginRegisterForm form) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        return user;
    }
}
