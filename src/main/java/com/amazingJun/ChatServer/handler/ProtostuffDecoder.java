package com.amazingJun.ChatServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @Auther: wujun
 * @Date: 2018/5/13 09:08
 * @Description:
 */
public class ProtostuffDecoder<T> extends LengthFieldBasedFrameDecoder {

    private Class<T> clazz;

    public ProtostuffDecoder(int maxObjectSize, Class<T> clazz) {
        super(maxObjectSize, 0, 4, 0, 4);
        this.clazz = clazz;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T t = schema.newMessage();

        try (ByteBufInputStream bin = new ByteBufInputStream(frame, true)) {
            ProtostuffIOUtil.mergeFrom(bin, t, schema);
            return t;
        }
    }
}
