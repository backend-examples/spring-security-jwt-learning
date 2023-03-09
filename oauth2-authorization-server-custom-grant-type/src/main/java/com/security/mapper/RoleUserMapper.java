package com.security.mapper;

import com.security.pojo.RoleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleUserMapper {

    void insertRoleUser(RoleUser roleUser);

    List<RoleUser> selectRoleUserByUserId(@Param("userId") Integer userId);
}
