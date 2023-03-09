package com.security.enums;

import com.security.constant.ScopeConstant;

import java.util.LinkedHashSet;
import java.util.Set;

public enum ScopeWithDescriptionEnum implements BaseScopeInfo {

    ALL(ScopeConstant.ALL_SCOPE, ScopeConstant.ALL_DESCRIPTION, true),
    USER_BASE(ScopeConstant.USER_BASE_SCOPE, ScopeConstant.USER_BASE_DESCRIPTION, true),
    PROFILE(ScopeConstant.PROFILE_SCOPE, ScopeConstant.PROFILE_DESCRIPTION, true),
    MESSAGE_READ(ScopeConstant.MESSAGE_READ_SCOPE, ScopeConstant.MESSAGE_READ_DESCRIPTION, true),
    MESSAGE_WRITE(ScopeConstant.MESSAGE_WRITE_SCOPE, ScopeConstant.MESSAGE_WRITE_DESCRIPTION, true);

    private final String scope;

    private final String description;

    private final boolean value;

    ScopeWithDescriptionEnum(String scope, String description, boolean value) {
        this.scope = scope;
        this.description = description;
        this.value = value;
    }

    public static Set<ScopeWithDescriptionEnum> withDescription(Set<String> scopes) {
        Set<ScopeWithDescriptionEnum> scopeWithDescriptions = new LinkedHashSet<>();
        ScopeWithDescriptionEnum[] scopeWithDescriptionEnums = values();
        for (ScopeWithDescriptionEnum scopeWithDescriptionEnum:scopeWithDescriptionEnums) {
            for (String scope:scopes) {
                if (scope.equals(scopeWithDescriptionEnum.getScope())) {
                    scopeWithDescriptions.add(scopeWithDescriptionEnum);
                }
            }
        }

        return scopeWithDescriptions;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean getValue() {
        return value;
    }


}
