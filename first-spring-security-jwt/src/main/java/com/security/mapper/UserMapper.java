package com.security.mapper;

import com.security.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(User user);

    void insertRoleUser(Integer roleId, Integer userId);

    User selectUserByUsername(String username);
}
