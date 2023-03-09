package com.security.service;

import com.security.model.LoginRegisterForm;
import com.security.model.SecurityUser;
import com.security.pojo.ClientDetail;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface UserService extends UserDetailsService {
    void registerUser(LoginRegisterForm form);

    void checkLogin(SecurityUser securityUser);

    Map<String, Object> login(LoginRegisterForm form);

    void getRandomCode(HttpServletResponse response) throws IOException;
}
