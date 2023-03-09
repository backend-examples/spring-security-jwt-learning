package com.security.service.impl;

import com.security.enums.CodeEnum;
import com.security.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 自定义鉴权服务
 * 用于@PreAuthorize注解，该注解可以接受表达式和自定义方法
 */
@Service("sc")
public class PermissionService {

    /**
     * 判断接口是否有xxx:xxx权限
     * @param permission 权限
     * @return {boolean}
     */
    public boolean hasPermission(String permission) {
        if (StringUtils.isEmpty(permission)) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new BizException(CodeEnum.NOT_AUTHENTICATION);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean auth = authorities.stream().map(GrantedAuthority::getAuthority).filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));

        return auth;
    }

}
