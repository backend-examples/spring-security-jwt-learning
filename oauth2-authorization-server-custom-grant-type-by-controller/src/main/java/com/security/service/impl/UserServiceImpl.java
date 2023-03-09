package com.security.service.impl;

import com.security.constant.Messages;
import com.security.constant.RedisConstant;
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
import com.security.utils.CaptchaUtils;
import com.security.utils.PhoneFormatCheckUtils;
import com.security.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialExpiredException;
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

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String usernameOrPhone) throws UsernameNotFoundException {
        User user;
        boolean isPhone = PhoneFormatCheckUtils.isPhoneLegal(usernameOrPhone);

        // 因为刷新token也使用了loadUserByUsername方法
        // 所以在这里判断是否是手机号
        if (isPhone) {
            user = userMapper.selectUserByPhone(usernameOrPhone);
        } else {
            user = userMapper.selectUserByUsername(usernameOrPhone);
        }

        if (user == null) {
            //用户不存在直接抛出UsernameNotFoundException，security会捕获抛出BadCredentialsException
            throw new UsernameNotFoundException(usernameOrPhone + "不存在！");
        }

        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(user.getId());

        // 手机号验证码登录时，刷新token同样返回手机号作为用户名
        securityUser.setUsername(isPhone ? user.getPhone() : user.getUsername());
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

        if (!securityUser.isEnabled()) {
            throw new DisabledException("该账号已被禁用");
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定");
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期");
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("该账号密码已过期");
        }

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
        // getRandomCodeImage方法会直接将生成的验证码图片写入response，3代表算术验证码只有两个因子
        String randomResult = CaptchaUtils.builder().arithmetic(3).getRandomCodeImage(response);
//        String randomResult = CaptchaUtils.builder().getRandomCodeImage(response);
        redisUtil.set(RedisConstant.REDIS_UMS_PREFIX, randomResult, RedisConstant.REDIS_UMS_EXPIRE);
    }

    @Override
    public SecurityUser getUserByUsernameAndPassword(String username, String password) {
//        User user = userMapper.selectUserByUsernameAndPassword();
        return null;
    }
}
