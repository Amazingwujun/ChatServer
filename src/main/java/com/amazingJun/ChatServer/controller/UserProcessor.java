package com.amazingJun.ChatServer.controller;

import com.amazingJun.ChatServer.common.annotation.MethodMapping;
import com.amazingJun.ChatServer.common.annotation.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jun
 * @date 2018-10-12 15:21
 */
@Slf4j
@Processor("user")
public class UserProcessor {

    @MethodMapping("login")
    public void login(ChannelHandlerContext ctx) {
        log.error(ctx.toString());
    }
}
