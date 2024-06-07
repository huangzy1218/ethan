package com.ethan.rpc.protocol.codec;

import com.ethan.remoting.Channel;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class EthanCodec implements Codec {

    @Override
    public void encode(Channel channel, ByteBuf buffer, Object message) throws IOException {

    }

    @Override
    public Object decode(Channel channel, ByteBuf buffer) throws IOException {
        return null;
    }

}
