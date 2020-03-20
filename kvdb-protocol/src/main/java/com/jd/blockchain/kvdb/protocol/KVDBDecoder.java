package com.jd.blockchain.kvdb.protocol;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KVDBDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KVDBDecoder.class);

    static {
        DataContractRegistry.register(Message.class);
        DataContractRegistry.register(Command.class);
        DataContractRegistry.register(MessageContent.class);
        DataContractRegistry.register(Response.class);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        out.add(BinaryProtocol.decodeAs(bytes, Message.class));
    }

}