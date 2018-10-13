package com.amazingJun.ChatServer.entity.bo;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author Jun
 * @date 2018-10-12 15:56
 */
@Data
public class ProcessorAndMethod {

    /**
     * 方法处理类
     */
    private Object target;

    /**
     * 处理的方法
     */
    private Method method;
}
