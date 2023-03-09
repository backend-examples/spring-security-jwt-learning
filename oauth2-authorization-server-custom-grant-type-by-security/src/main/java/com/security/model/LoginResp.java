package com.security.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class LoginResp {

    private String accessToken;

    private String tokenType;

    private String refreshToken;

    private Integer expiresIn;

    private Set<String> scope;

    private String jti;

}