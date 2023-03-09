package com.security.service.impl;

import com.security.constant.Messages;
import com.security.enums.CodeEnum;
import com.security.exception.BizException;
import com.security.mapper.RoleMapper;
import com.security.mapper.RoleUserMapper;
import com.security.mapper.UserMapper;
import com.security.model.LoginRegisterForm;
import com.security.model.SecurityUser;
import com.security.pojo.RoleUser;
import com.security.pojo.User;
import com.security.service.UserService;
import com.security.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void registerUser(LoginRegisterForm form) {
        User user = form.toUser(form);

        User exitedUser = userMapper.selectUserByUsername(user.getUsername());

        if (exitedUser != null) {
            throw new BizException(CodeEnum.SUCCESS.getCode(), Messages.ACCOUNT_HAS_EXIST);
        }

        userMapper.insertUser(user);

        RoleUser roleUser = new RoleUser();
        roleUser.setUserId(user.getId());
        roleUser.setRoleId(form.getRoleId());

        roleUserMapper.insertRoleUser(roleUser);
    }

    @Override
    public Map<String, Object> login(LoginRegisterForm form) {
        // 用户验证
        Authentication authentication = null;
        try {
            // 下面操作主要用于登录的时候方便全局获取用户认证信息。而TokenAuthenticationFilter中同样的操作，则是方便登录后全局使用认证对象去获取用户信息。
            // 这里存储的是从前端发送过来的用户和密码
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword());
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
            // 将返回的Authentication存到上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                throw new BizException(CodeEnum.USERNAME_PASSWORD_ERROR);
            } else {
                throw new BizException(e.getMessage());
            }
        }  finally {
            // 无论结果如何，清除全局上下文
            SecurityContextHolder.clearContext();
        }

        Map<String, Object> token = new HashMap<>();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // 生成token
        token.put("access-token", jwtUtils.createToken(securityUser));
        //生成刷新令牌，如果accessToken令牌失效，则使用refreshToken重新获取令牌（refreshToken过期时间必须大于accessToken）
        token.put("refresh-token", jwtUtils.refreshToken(jwtUtils.createToken(securityUser)));

        return token;
    }

    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectUserByUsername(username);

        if (user == null) {
            //用户不存在直接抛出UsernameNotFoundException，security会捕获抛出BadCredentialsException
            throw new UsernameNotFoundException(username + "不存在！");
        }

        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(user.getId());
        securityUser.setUsername(user.getUsername());
        //todo 此处为了方便，直接在数据库存储的明文，实际生产中应该存储密文，则这里不用再次加密
        securityUser.setPassword(user.getPassword());
        List<RoleUser> roleUserList = roleUserMapper.selectRoleUserByUserId(user.getId());
        List<String> roleList = new ArrayList<>();
        String[] authoritiesArray = {};
        for (RoleUser roleUser : roleUserList) {
            roleList.add(roleMapper.selectRoleById(roleUser.getRoleId()).getRoleName());
        }
        //获取权限集合
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleList.toArray(authoritiesArray));
        securityUser.setAuthorities(authorities);

        return securityUser;
    }
}
