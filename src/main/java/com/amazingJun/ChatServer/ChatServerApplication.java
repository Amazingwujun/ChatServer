package com.amazingJun.ChatServer;

import com.amazingJun.ChatServer.common.annotation.MethodMapping;
import com.amazingJun.ChatServer.common.annotation.Processor;
import com.amazingJun.ChatServer.entity.bo.ProcessorAndMethod;
import com.amazingJun.ChatServer.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jun
 */
@Slf4j
@MapperScan("com.amazingJun.ChatServer.dao")
@SpringBootApplication
public class ChatServerApplication {
    /**
     * 方法映射
     */
    public static final ConcurrentHashMap<String, ProcessorAndMethod> METHOD_MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = SpringApplication.run(ChatServerApplication.class, args);

        init(ctx);

        NettyServerInitializer server = ctx.getBean(NettyServerInitializer.class);
        server.start();
    }

    private static void init(final ConfigurableApplicationContext ctx) {
        log.info("开始注册处理器方法");

        // 初始化方法映射
        Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(Processor.class);

        //获取处理器
        Collection<Object> objects = beansWithAnnotation.values();

        //将合法的处理器注册到 METHOD_MAP
        objects.forEach(e -> {

            final String urlPrefix = e.getClass().getAnnotation(Processor.class).value();

            Method[] methods = e.getClass().getMethods();

            for (Method method : methods) {
                MethodMapping annotation = method.getAnnotation(MethodMapping.class);
                if (!Objects.isNull(annotation)) {
                    //获取方法映射
                    String url = urlPrefix + annotation.value();

                    if (StringUtils.isEmpty(url) || METHOD_MAP.containsKey(url)) {
                        throw new GlobalException("方法映射不能为空或重复");
                    }

                    ProcessorAndMethod processorAndMethod = new ProcessorAndMethod();
                    processorAndMethod.setTarget(e);
                    processorAndMethod.setMethod(method);

                    METHOD_MAP.put(url, processorAndMethod);
                }
            }
        });

        log.info("处理器方法注册结束");
    }
}
