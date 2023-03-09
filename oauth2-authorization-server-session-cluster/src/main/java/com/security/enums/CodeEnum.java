package com.security.enums;

import com.security.constant.Messages;

/**
 * 数据响应状态码
 */
public enum CodeEnum implements BaseErrorInfo {
    SUCCESS(200, Messages.SUCCESS),
    NOT_AUTHENTICATION(305, Messages.NOT_AUTHENTICATION),
    BODY_NOT_MATCH(400, Messages.PARAMS_ERROR),
    SIGNATURE_NOT_MATCH(401, Messages.SIGNATURE_MISMATCH),
    FORBIDDEN(403, Messages.FORBIDDEN),
    NOT_FOUND(404, Messages.NOT_FOUND),
    USERNAME_PASSWORD_ERROR(405, Messages.USERNAME_PASSWORD_ERROR),
    ACCOUNT_EXPIRED(406, Messages.ACCOUNT_EXPIRED),
    ACCOUNT_DISABLE(407, Messages.ACCOUNT_DISABLE),
    ACCOUNT_LOCKED(408, Messages.ACCOUNT_LOCKED),
    ACCOUNT_NOT_EXIST(410, Messages.ACCOUNT_NOT_EXIST),
    INTERNAL_SERVER_ERROR(500, Messages.INTERNAL_SERVER_ERROR),
    SERVER_BUSY(503, Messages.SERVER_BUSY);

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误描述
     */
    private String message;

    CodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
