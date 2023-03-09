package com.security.utils;

import com.alibaba.fastjson2.JSONObject;
import com.security.enums.CodeEnum;
import lombok.Data;

/**
 * 自定义数据格式
 * 使用建造者模式重写了
 * @param <T>
 */
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

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public ResponseResult(Builder<T> builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 建造者 内部静态类
     * @param <R>
     */
    public static final class Builder<R> {

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
        private R data;

        public Builder() {}

        public Builder<R> code(Integer code) {
            this.code = code;

            return this;
        }

        public Builder<R> message(String message) {
            this.message = message;

            return this;
        }

        public Builder<R> data(R data) {
            this.data = data;

            return this;
        }

        public Builder<R> ok() {
            this.code = CodeEnum.SUCCESS.getCode();
            this.message = CodeEnum.SUCCESS.getMessage();
            this.data = null;

            return this;
        }

        public Builder<R> ok(R data) {
            this.code = CodeEnum.SUCCESS.getCode();
            this.message = CodeEnum.SUCCESS.getMessage();
            this.data = data;

            return this;
        }

        public Builder<R> ok(String message, R data) {
            this.code = CodeEnum.SUCCESS.getCode();
            this.message = message;
            this.data = data;

            return this;
        }

        public Builder<R> error() {
            this.code = CodeEnum.BODY_NOT_MATCH.getCode();
            this.message = CodeEnum.BODY_NOT_MATCH.getMessage();
            this.data = null;

            return this;
        }

        public Builder<R> error(String message) {
            this.code = CodeEnum.BODY_NOT_MATCH.getCode();
            this.message = message;
            this.data = null;

            return this;
        }

        public Builder<R> error(Integer code, String message) {
            this.code = code;
            this.message = message;
            this.data = null;

            return this;
        }

        public Builder<R> fail() {
            this.code = CodeEnum.INTERNAL_SERVER_ERROR.getCode();
            this.message = CodeEnum.INTERNAL_SERVER_ERROR.getMessage();
            this.data = null;

            return this;
        }

        public Builder<R> fail(String message) {
            this.code = CodeEnum.INTERNAL_SERVER_ERROR.getCode();
            this.message = message;
            this.data = null;

            return this;
        }

        public ResponseResult<R> build() {
            return new ResponseResult<>(this);
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
