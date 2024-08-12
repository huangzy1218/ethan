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
            if (msg instanceof ByteBuf byteBuf) {
                out.writeBytes(byteBuf);
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
//            codec.decode(channel, input);
//            // Decode object
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

//    private static class InternalEncoder extends MessageToByteEncoder<String> {
//        @Override
//        protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
//            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
//            out.writeBytes(bytes);
//        }
//    }
//
//    private static class InternalDecoder extends ByteToMessageDecoder {
//        @Override
//        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//            if (in.readableBytes() > 0) {
//                byte[] bytes = new byte[in.readableBytes()];
//                in.readBytes(bytes);
//                String decoded = new String(bytes, StandardCharsets.UTF_8);
//                out.add(decoded);
//            }
//        }
//    }

}
