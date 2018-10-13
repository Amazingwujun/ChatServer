package com.amazingJun.ChatServer.entity.dto;

import lombok.Data;

/**
 * @author Jun
 * @date 2018-10-12 14:23
 */
@Data
public class Message<T> {

    private Header header;

    private T body;
}
