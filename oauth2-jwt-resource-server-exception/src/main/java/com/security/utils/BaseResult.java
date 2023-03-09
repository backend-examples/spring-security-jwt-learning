package com.security.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    protected boolean success;

    /**
     * 错误信息
     */
    protected String message;

    /**
     * 详细信息
     */
    protected String detailMessage;

    /**
     * 返回数据
     */
    protected Object data;
}

