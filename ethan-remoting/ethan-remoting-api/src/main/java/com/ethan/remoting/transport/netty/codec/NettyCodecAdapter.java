package com.ethan.remoting.transport.netty.codec;

import com.ethan.common.URL;
import com.ethan.remoting.Codec;
import com.ethan.remoting.transport.netty.NettyChannel;
import com.ethan.rpc.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Netty codec adapter.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyCodecAdapter {

    private final ChannelHandler encoder = new InternalEncoder();

    private final ChannelHandler decoder = new InternalDecoder();

    private final URL url;
    private final Codec codec;

    public NettyCodecAdapter(Codec codec, URL url) {
        this.codec = codec;
        this.url = url;
    }

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    private class InternalEncoder extends MessageToByteEncoder<Message> {

        @Override
        protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
            Channel ch = ctx.channel();
            NettyChannel channel = NettyChannel.getOrAddChannel(ch, url);
            codec.encode(channel, out, msg);
        }
    }

    private class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
            NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url);
            // Decode object
            do {
                Object msg = codec.decode(channel, input);
            } while (input.isReadable());
        }
    }

}
