package com.security.service;

import com.security.exception.BizException;
import com.security.mapper.RoleMapper;
import com.security.mapper.UserMapper;
import com.security.model.LoginRegisterForm;
import com.security.model.SecurityUser;
import com.security.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author 公众号：码猿技术专栏
 * 从数据库中根据用户名查询用户的详细信息，包括权限
 *
 * 数据库设计：角色、用户、权限、角色<->权限、用户<->角色 总共五张表，遵循RBAC设计
 */
@Service
public class JwtTokenUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectUserByUsername(username);

        if (user != null) {
            SecurityUser securityUser = new SecurityUser();
            securityUser.setUsername(username);
            //todo 此处为了方便，直接在数据库存储的明文，实际生产中应该存储密文，则这里不用再次加密
            securityUser.setPassword(user.getPassword());
            //获取权限集合
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getRoleName());
            securityUser.setAuthorities(authorities);

            return securityUser;
        }


        //用户不存在直接抛出UsernameNotFoundException，security会捕获抛出BadCredentialsException
        throw new UsernameNotFoundException(username + "不存在！");
    }

    public void registerUser(LoginRegisterForm form) {
        User user = form.toUser(form);

        User exitedUser = userMapper.selectUserByUsername(user.getUsername());

        if (exitedUser == null) {
            userMapper.insertUser(user);
            userMapper.insertRoleUser(form.getRoleId(), user.getId());

            return;
        }

        System.out.println("user: " + user);
        throw new BizException(200, "用户已存在");
    }

    /**
     * 合并role和auth
     * @param roles
     * @param auths
     * @return
     */
    private Collection<? extends GrantedAuthority> merge(List<String> roles, List<String> auths){
        String[] a={};
        roles.addAll(auths);
        return AuthorityUtils.createAuthorityList(roles.toArray(a));
    }
}
