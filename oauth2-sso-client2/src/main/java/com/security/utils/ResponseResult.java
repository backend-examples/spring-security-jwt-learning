package com.security.utils;

import com.alibaba.fastjson2.JSONObject;
import com.security.enums.CodeEnum;
import lombok.Data;

/**
 * 自定义数据格式
 *
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
     *
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
     * @param
     * @return
     */
    public ResponseResult success() {
        this.setCode(CodeEnum.SUCCESS.getCode());
        this.setMessage(CodeEnum.SUCCESS.getMessage());
        this.setData(null);

        return this;
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public ResponseResult success(T data) {
        this.setCode(CodeEnum.SUCCESS.getCode());
        this.setMessage(CodeEnum.SUCCESS.getMessage());
        this.setData(data);

        return this;
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public ResponseResult success(String message, T data) {
        this.setCode(CodeEnum.SUCCESS.getCode());
        this.setMessage(message);
        this.setData(data);

        return this;
    }

    /**
     * 失败
     *
     * @param data
     * @return
     */
    public ResponseResult error(T data) {
        this.setCode(CodeEnum.BODY_NOT_MATCH.getCode());
        this.setMessage(CodeEnum.BODY_NOT_MATCH.getMessage());
        this.setData(null);

        return this;
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public ResponseResult error(Integer code, String message) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(null);

        return this;
    }

    /**
     * 失败
     *
     * @param message
     * @return
     */
    public ResponseResult error(String message) {
        this.setCode(CodeEnum.BODY_NOT_MATCH.getCode());
        this.setMessage(message);
        this.setData(null);

        return this;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
