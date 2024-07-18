package com.ethan.remoting.transport.netty.codec;

import com.ethan.common.URL;
import com.ethan.remoting.Codec;
import com.ethan.remoting.transport.netty.NettyChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Netty codec adapter.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyCodecAdapter {

    @Getter
    private final ChannelHandler encoder = new InternalEncoder();

    @Getter
    private final ChannelHandler decoder = new InternalDecoder();

    private final URL url;
    private final Codec codec;

    public NettyCodecAdapter(Codec codec, URL url) {
        this.codec = codec;
        this.url = url;
    }

    private class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            boolean encoded = false;
            if (msg instanceof ByteBuf) {
                out.writeBytes(((ByteBuf) msg));
                encoded = true;
            }

            if (!encoded) {
                Channel ch = ctx.channel();
                NettyChannel channel = NettyChannel.getOrAddChannel(ch, url);
                codec.encode(channel, out, msg);
            }
        }
    }

    private class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
            NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url);
            // Decode object
            do {
                int saveReaderIndex = input.readerIndex();
                Object msg = codec.decode(channel, input);
                if (msg == Codec.DecodeResult.NEED_MORE_INPUT) {
                    input.readerIndex(saveReaderIndex);
                    break;
                } else {
                    if (saveReaderIndex == input.readerIndex()) {
                        throw new IOException("Decode without read data.");
                    }
                    if (msg != null) {
                        out.add(msg);
                    }
                }
            } while (input.isReadable());
        }
    }

}
