package com.security.model;

import com.security.constant.JwtConstant;
import com.security.pojo.ClientDetail;
import com.security.utils.EncodeUtils;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientDetailForm {

    private String username;

    private String clientId;

    private String resourceIds;

    private String clientSecret;

    private String scope;

    private String authorizedGrantTypes;

    private String webServerRedirectUri;

    private String authorities;

    private String additionalInformation;

    private String autoApprove;

    public ClientDetail toClientDetail(ClientDetailForm clientDetailForm) {
        ClientDetail clientDetail = new ClientDetail();

        clientDetail.setClientId(clientDetailForm.getClientId());

        if (clientDetailForm.getClientId() == null) {
            clientDetail.setClientId(EncodeUtils.encode(clientDetailForm.getUsername(), EncodeUtils.getSalt()));
        }

        clientDetail.setResourceIds(clientDetailForm.getResourceIds());
        clientDetail.setClientSecret(null);
        clientDetail.setScope(clientDetailForm.getScope());
        clientDetail.setAuthorizedGrantTypes(clientDetailForm.getAuthorizedGrantTypes());
        clientDetail.setWebServerRedirectUri(clientDetailForm.getWebServerRedirectUri());
        clientDetail.setAuthorities(clientDetailForm.getAuthorities());
        clientDetail.setAdditionalInformation(clientDetailForm.getAdditionalInformation());
        clientDetail.setAutoApprove(String.valueOf(false));
        clientDetail.setAccessTokenValidity(JwtConstant.ACCESS_TOKEN_EXPIRE_TIME);
        clientDetail.setRefreshTokenValidity(JwtConstant.REFRESH_TOKEN_EXPIRE_TIME);

        return clientDetail;
    }
}
