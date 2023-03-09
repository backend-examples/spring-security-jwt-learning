package com.security.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author 公众号：码猿技术专栏
 * 存储用户的详细信息，实现UserDetails，后续有定制的字段可以自己拓展
 */
@Data
public class SecurityUser implements UserDetails, Serializable {

    private static final long serialVersionUID = 9178661439383356177L;

    // 用户id
    private Integer id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 用户的uuid，用于禁止用于多端登录
    private String uuid;

    // 用户登录时间
    private Long loginTime;

    // token失效时间
    private Long expireTime;

    //权限+角色集合
//    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    private Collection<? extends GrantedAuthority> authorities;

    public SecurityUser() {
    }

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
//    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账户是否未被锁
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
