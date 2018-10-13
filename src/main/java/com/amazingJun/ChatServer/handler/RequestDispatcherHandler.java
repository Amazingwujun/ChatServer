package com.amazingJun.ChatServer.handler;

import com.amazingJun.ChatServer.ChatServerApplication;
import com.amazingJun.ChatServer.common.constant.ServerMessageType;
import com.amazingJun.ChatServer.entity.bo.ProcessorAndMethod;
import com.amazingJun.ChatServer.entity.dto.Header;
import com.amazingJun.ChatServer.entity.dto.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Jun
 * @date 2018-10-04 11:25
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class RequestDispatcherHandler extends SimpleChannelInboundHandler<Message> {

    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Map<String, ProcessorAndMethod> methodMap = ChatServerApplication.METHOD_MAP;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //判断请求是否合法
        Header header = msg.getHeader();

        if (!methodMap.containsKey(header.getMethod())) {
            ctx.writeAndFlush(buildErrorMessage(ServerMessageType.ERROR_RESP));
            return;
        }

        ProcessorAndMethod processorAndMethod = methodMap.get(header.getMethod());

        Object target = processorAndMethod.getTarget();
        Method method = processorAndMethod.getMethod();

        Object result;
        try {
            result = method.invoke(target, ctx);
        } catch (IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw e;
        }


        System.out.println(msg);
        msg.setBody("你是猪吗");
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
    }

    private Message buildErrorMessage(ServerMessageType serverMessageType) {
        Message message = new Message();
        Header header = new Header();

        header.setType(serverMessageType.getCode());
        header.setMsg("找不到请求处理方法");
        message.setHeader(header);
        return message;
    }

}
