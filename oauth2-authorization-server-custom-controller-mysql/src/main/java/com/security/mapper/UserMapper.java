package com.security.mapper;

import com.security.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(User user);

    void insertRoleUser(Integer roleId, Integer userId);

    User selectUserByUsername(@Param("username") String username);
}
