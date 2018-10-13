package com.amazingJun.ChatServer.handler;

import com.amazingJun.ChatServer.entity.dto.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.stereotype.Component;

/**
 * protostuff 序列化对象最大 65M
 *
 * @Auther: wujun
 * @Date: 2018/5/13 08:36
 * @Description:
 */
@Component
@ChannelHandler.Sharable
public class ProtostuffEncoder extends MessageToByteEncoder<Message> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        int startIdx = out.writerIndex();

        ByteBufOutputStream bout = new ByteBufOutputStream(out);

        Schema<Message> schema = RuntimeSchema.getSchema(Message.class);

        LinkedBuffer buff = LinkedBuffer.allocate();

        final byte[] protostuff;

        try {
            bout.write(LENGTH_PLACEHOLDER);
            protostuff = ProtostuffIOUtil.toByteArray(msg, schema, buff);
            bout.write(protostuff);
        } finally {
            buff.clear();
            bout.close();
        }

        int endIdx = out.writerIndex();

        out.setInt(startIdx, endIdx - startIdx - 4);
    }
}
