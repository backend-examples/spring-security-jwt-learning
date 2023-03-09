package com.security.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class User {

    private Integer id;

    private String username;

    private String password;

    private String roleName;
}
