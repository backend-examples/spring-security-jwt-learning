package com.security.exception;

import com.security.enums.BaseErrorInfo;

/**
 * 自定义异常类
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public BizException() {
        super();
    }

    public BizException(BaseErrorInfo errorInfo) {
        super(String.valueOf(errorInfo.getCode()));
        this.errorCode = errorInfo.getCode();
        this.errorMsg = errorInfo.getMessage();
    }

    public BizException(BaseErrorInfo errorInfo, Throwable cause) {
        super(String.valueOf(errorInfo.getCode()), cause);
        this.errorCode = errorInfo.getCode();
        this.errorMsg = errorInfo.getMessage();
    }

    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorCode = 500;
        this.errorMsg = errorMsg;
    }

    public BizException(Integer errorCode, String errorMsg) {
        super(String.valueOf(errorCode));
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BizException(Integer errorCode, String errorMsg, Throwable cause) {
        super(String.valueOf(errorCode), cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMessage() {
        return errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}