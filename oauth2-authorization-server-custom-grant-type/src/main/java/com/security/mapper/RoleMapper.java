package com.security.mapper;

import com.security.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {
    Role selectRoleById(@Param("id") Integer id);
}
