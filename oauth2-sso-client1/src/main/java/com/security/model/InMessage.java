package com.security.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class InMessage {
    /**
     * 从哪里来
     */
    private String from;

    /**
     * 到哪里去
     */
    private String to;

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
