package com.security.mapper;

import com.security.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper {
    Role selectRoleById(Integer id);
}
