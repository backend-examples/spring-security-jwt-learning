package com.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailsModel implements ClientDetails {

    private String clientId;

    private Set<String> resourceIds;

    private String clientSecret;

    private Set<String> scope;

    private Set<String> authorizedGrantTypes;

    private Set<String> registeredRedirectUris;

    private Collection<GrantedAuthority> authorities;

    private Integer accessTokenValiditySeconds;

    private Integer refreshTokenValiditySeconds;

    private boolean autoApprove;

    private Map<String, Object> additionalInformation;

    public ClientDetailsModel(String clientId, String resourceIds, String scopes, String grantTypes, String authorities, String redirectUris) {
        this.scope = Collections.emptySet();
        this.resourceIds = Collections.emptySet();
        this.authorizedGrantTypes = Collections.emptySet();
        this.authorities = Collections.emptyList();
        this.additionalInformation = new LinkedHashMap();
        this.clientId = clientId;
        Set<String> scopeList;

        if (StringUtils.hasText(resourceIds)) {
            scopeList = StringUtils.commaDelimitedListToSet(resourceIds);
            if (!scopeList.isEmpty()) {
                this.resourceIds = scopeList;
            }
        }

        if (StringUtils.hasText(scopes)) {
            scopeList = StringUtils.commaDelimitedListToSet(scopes);
            if (!scopeList.isEmpty()) {
                this.scope = scopeList;
            }
        }

        if (StringUtils.hasText(grantTypes)) {
            this.authorizedGrantTypes = StringUtils.commaDelimitedListToSet(grantTypes);
        } else {
            this.authorizedGrantTypes = new HashSet(Arrays.asList("authorization_code", "refresh_token"));
        }

        if (StringUtils.hasText(authorities)) {
            this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
        }

        if (StringUtils.hasText(redirectUris)) {
            this.registeredRedirectUris = StringUtils.commaDelimitedListToSet(redirectUris);
        }
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = (Set)(resourceIds == null ? Collections.emptySet() : new LinkedHashSet(resourceIds));
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = new LinkedHashSet(authorizedGrantTypes);
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setRegisteredRedirectUris(Set<String> registeredRedirectUris) {
        this.registeredRedirectUris = registeredRedirectUris;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUris;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String s) {
        return autoApprove;
    }

    public void setAdditionalInformation(Map<String, ?> additionalInformation) {
        this.additionalInformation = new LinkedHashMap(additionalInformation);;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }
}
