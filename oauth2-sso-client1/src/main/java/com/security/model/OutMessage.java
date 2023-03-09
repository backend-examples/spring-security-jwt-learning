package com.security.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OutMessage {
    /**
     * 从哪里来
     */
    private String from;

    /**
     * 内容
     */
    private String content;

    /**
     * 时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
}
