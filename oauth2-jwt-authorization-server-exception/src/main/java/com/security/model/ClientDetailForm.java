package com.security.model;

import com.security.pojo.ClientDetail;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientDetailForm {

    private String clientId;

    private String resourceIds;

    private String clientSecret;

    private String scope;

    private String authorizedGrantTypes;

    private String webServerRedirectUri;

    private String authorities;

    private String additionalInformation;

    private String autoApprove;

    public ClientDetail toClientDetail(ClientDetailForm clientDetailModel) {
        ClientDetail clientDetail = new ClientDetail();

//        clientDetail.setClientId(clientDetailModel.getClientId());
//        clientDetail.setResourceIds(clientDetailModel.getResourceIds());
//        clientDetail.setClientSecret(clientDetailModel.getClientSecret());
//        clientDetail.setScope(clientDetailModel.getScope());
//        clientDetail.setAuthorizedGrantTypes(clientDetailModel.getAuthorizedGrantTypes());
//        clientDetail.setWebServerRedirectUri(clientDetailModel.getWebServerRedirectUri());
//        clientDetail.setAuthorities(clientDetailModel.getAuthorities());
//        clientDetail.setAdditionalInformation(clientDetailModel.getAdditionalInformation());
//        clientDetail.setAutoApprove(clientDetailModel.getAutoApprove());
//        clientDetail.setAccessTokenValidity(JwtConstant.ACCESS_TOKEN_EXPIRE_TIME);
//        clientDetail.setRefreshTokenValidity(JwtConstant.REFRESH_TOKEN_EXPIRE_TIME);

        return clientDetail;
    }
}
