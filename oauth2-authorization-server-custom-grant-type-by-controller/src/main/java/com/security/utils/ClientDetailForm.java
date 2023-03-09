package com.security.utils;

import com.security.constant.JwtConstant;
import com.security.constant.SecurityConstant;
import com.security.pojo.ClientDetail;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;

@Data
@ToString
public class ClientDetailForm {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String resourceIds;

    private String scope;

    private String authorizedGrantTypes;

    @NotBlank(message = "url不能为空")
    private String url;

    private String authorities;

    private String additionalInformation;

    private String autoApprove;

    public ClientDetail toClientDetail(ClientDetailForm clientDetailModel) {
        ClientDetail clientDetail = new ClientDetail();

        clientDetail.setClientId(EncodeUtils.encode(clientDetailModel.getUsername(), EncodeUtils.getSalt()));
        clientDetail.setResourceIds(clientDetailModel.getResourceIds());

        // 每次调用new BCryptPasswordEncoder()都会生成一个独一无二的盐
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = EncodeUtils.encode(clientDetailModel.getPassword(), EncodeUtils.getSalt());
        clientDetail.setClientSecret(passwordEncoder.encode(password));
        clientDetail.setPlainClientSecret(password);

        clientDetail.setScope(SecurityConstant.DEFAULT_SCOPE);
        if (clientDetailModel.getScope() != null) {
            clientDetail.setScope(clientDetailModel.getScope());
        }

        clientDetail.setAuthorizedGrantTypes(SecurityConstant.DEFAULT_AUTHORIZED_GRANT_TYPES);
        if (clientDetailModel.getAuthorizedGrantTypes() != null) {
            clientDetail.setAuthorizedGrantTypes(clientDetailModel.getAuthorizedGrantTypes());
        }

        clientDetail.setWebServerRedirectUri(clientDetailModel.getUrl());
        clientDetail.setAuthorities(clientDetailModel.getAuthorities());
        clientDetail.setAdditionalInformation(clientDetailModel.getAdditionalInformation());
        clientDetail.setAutoApprove(String.valueOf(false));
        clientDetail.setAccessTokenValidity(JwtConstant.ACCESS_TOKEN_EXPIRE_TIME);
        clientDetail.setRefreshTokenValidity(JwtConstant.REFRESH_TOKEN_EXPIRE_TIME);

        return clientDetail;
    }
}
