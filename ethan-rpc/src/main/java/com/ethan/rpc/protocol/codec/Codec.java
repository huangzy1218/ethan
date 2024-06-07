package com.ethan.rpc.protocol.codec;

import com.ethan.remoting.Channel;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Codec {

    void encode(Channel channel, ByteBuf buffer, Object message) throws IOException;

    Object decode(Channel channel, ByteBuf buffer) throws IOException;

}
