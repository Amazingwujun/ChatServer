package com.amazingJun.ChatServer;


import com.amazingJun.ChatServer.entity.dto.Message;
import com.amazingJun.ChatServer.handler.HeartBeatHandler;
import com.amazingJun.ChatServer.handler.ProtostuffDecoder;
import com.amazingJun.ChatServer.handler.ProtostuffEncoder;
import com.amazingJun.ChatServer.handler.RequestDispatcherHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * netty服务启动类
 *
 * @author Jun
 * @date 2018-10-12 13:20
 */
@Component
public class NettyServerInitializer {

    @Value("${netty.server.port}")
    private int port;

    @Value("${netty.server.ip}")
    private String ip;

    /**
     * 传输对象最大值，默认10M
     */
    private final int MAX_OBJECT_SIZE = 10 * 1024 * 1024;

    /**
     * 客户端心跳超时，默认30秒
     */
    private final int HEARTBEAT_READ_TIMEOUT = 30;

    /**
     * 业务线程池大小，默认抓取当前环境处理器数量
     */
    private final int BUSINESS_THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    @Autowired
    private ProtostuffEncoder protostuffEncoder;

    @Autowired
    private RequestDispatcherHandler requestDispatcherHandler;

    @Autowired
    private HeartBeatHandler heartBeatHandler;

    /**
     * 服务启动
     *
     * @throws InterruptedException
     */
    void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(4);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        //业务执行线程池
        EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(BUSINESS_THREAD_SIZE,
                new DefaultThreadFactory("BusinessEventExecutor"));
        try {
            ServerBootstrap b = new ServerBootstrap();

            b
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(protostuffEncoder);
                            pipeline.addLast(new ProtostuffDecoder<>(MAX_OBJECT_SIZE, Message.class));
                            pipeline.addLast(new ReadTimeoutHandler(HEARTBEAT_READ_TIMEOUT));
                            pipeline.addLast(heartBeatHandler);
                            pipeline.addLast(eventExecutors, requestDispatcherHandler);
                        }
                    });

            Channel channel = b.bind(ip, port).sync().channel();

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
