package com.security.service.impl;

import com.security.exception.BizException;
import com.security.service.PermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Service("pms")
public class PermissionServiceImpl implements PermissionService {

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        // 获取主体
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean flag;

        try {
            flag = authorities.contains(new SimpleGrantedAuthority(request.getRequestURI()));
        } catch (Exception e) {
            throw new BizException("异常: " + e.getCause().getMessage());
        }

        return flag;
    }
}
