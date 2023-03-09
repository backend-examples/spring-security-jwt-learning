package com.security.enums;

public interface BaseScopeInfo {

    /**
     * @Feild: 权限名称
     * @return
     */
    String getScope();

    /**
     * @Feild: 权限描述
     * @return
     */
    String getDescription();

    /**
     * @Feild: 权限默认值
     * @return
     */
    boolean getValue();
}
