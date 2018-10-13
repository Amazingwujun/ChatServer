package com.amazingJun.ChatServer.entity.dto;

import lombok.Data;

/**
 * @author Jun
 * @date 2018-10-12 14:24
 */
@Data
public class Header {
    //@formatter:off

    /** 消息类型 */
    private byte type;

    /** 用户ID */
    private int userId;

    /** 对应的处理方法 */
    private String method;

    /** 响应消息 */
    private String msg;
}
