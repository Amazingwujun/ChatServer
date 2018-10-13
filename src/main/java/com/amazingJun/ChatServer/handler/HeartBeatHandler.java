package com.amazingJun.ChatServer.handler;

import com.amazingJun.ChatServer.common.constant.ServerMessageType;
import com.amazingJun.ChatServer.entity.dto.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Jun
 * @date 2018-10-04 11:22
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartBeatHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getHeader() != null && msg.getHeader().getType() == ServerMessageType.HEARTBEAT_REQ.getCode()) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }
}
