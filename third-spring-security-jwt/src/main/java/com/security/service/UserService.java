package com.security.service;

import com.security.model.LoginRegisterForm;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface UserService extends UserDetailsService {

    void registerUser(LoginRegisterForm form);

    Map<String, Object> login(LoginRegisterForm form);
}
