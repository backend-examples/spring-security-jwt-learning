package com.security.service.impl;

import com.security.constant.Messages;
import com.security.enums.CodeEnum;
import com.security.exception.BizException;
import com.security.mapper.ClientDetailMapper;
import com.security.mapper.RoleMapper;
import com.security.mapper.RoleUserMapper;
import com.security.mapper.UserMapper;
import com.security.model.LoginRegisterForm;
import com.security.model.SecurityUser;
import com.security.pojo.ClientDetail;
import com.security.pojo.RoleUser;
import com.security.service.UserService;
import com.security.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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

        // 这里返回的用户名和密码即登录时候用到的
        return securityUser;
    }

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
    public void checkLogin(SecurityUser securityUser) {

    }

    @Override
    public Map<String, Object> login(LoginRegisterForm form) {
        return null;
    }

    @Override
    public void getRandomCode(HttpServletResponse response) throws IOException {

    }
}
