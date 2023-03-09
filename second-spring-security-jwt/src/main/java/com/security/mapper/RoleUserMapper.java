package com.security.mapper;

import com.security.pojo.RoleUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleUserMapper {

    void insertRoleUser(RoleUser roleUser);

    List<RoleUser> selectRoleUserByUserId(Integer userId);
}
