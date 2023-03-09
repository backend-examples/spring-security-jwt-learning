package com.security.utils;


import com.alibaba.fastjson2.JSONObject;
import com.security.enums.BaseErrorInfo;
import com.security.enums.CodeEnum;
import lombok.Data;

/**
 * 自定义数据格式
 * @param <T>
 */
@Data
public class ResponseResult<T> {
    /**
     * 响应代码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 对象构造器，创建一个ResponseResult对象用来链式调用
     * @return
     */
    public static ResponseResult builder() {
        ResponseResult responseResult = new ResponseResult();

        return responseResult;
    }

    /**
     * 设置状态码
     */
    public ResponseResult code(Integer code) {
        this.setCode(code);

        return this;
    }


    /**
     * 设置响应消息
     */
    public ResponseResult message(String message) {
        this.setMessage(message);

        return this;
    }

    /**
     * 设置响应数据
     */
    public ResponseResult data(T data) {
        this.setData(data);

        return this;
    }

    /**
     * 成功
     *
     * @return
     */
    public static ResponseResult success() {
        return success(null);
    }

    /**
     * 成功
     * @param data
     * @return
     */
    public static ResponseResult success(Object data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(CodeEnum.SUCCESS.getCode());
        responseResult.setMessage(CodeEnum.SUCCESS.getMessage());
        responseResult.setData(data);

        return responseResult;
    }

    /**
     * 失败
     * @param errorInfo
     * @return
     */
    public static ResponseResult error(BaseErrorInfo errorInfo) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(errorInfo.getCode());
        responseResult.setMessage(errorInfo.getMessage());
        responseResult.setData(null);

        return responseResult;
    }

    /**
     * 失败
     * @param code
     * @param message
     * @return
     */
    public static ResponseResult error(Integer code, String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(code);
        responseResult.setMessage(message);
        responseResult.setData(null);
        return responseResult;
    }

    /**
     * 失败
     * @param message
     * @return
     */
    public static ResponseResult error(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(CodeEnum.INTERNAL_SERVER_ERROR.getCode());
        responseResult.setMessage(message);
        responseResult.setData(null);

        return responseResult;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
