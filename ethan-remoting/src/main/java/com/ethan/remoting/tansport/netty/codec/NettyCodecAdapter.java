package com.ethan.remoting.tansport.netty.codec;

import com.ethan.common.URL;
import com.ethan.remoting.tansport.netty.NettyChannel;
import com.ethan.rpc.protocol.codec.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import static com.ethan.remoting.RpcConstants.TOTAL_LENGTH;

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

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    private Codec codec;

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

    private class InternalDecoder extends LengthFieldBasedFrameDecoder {

        public InternalDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
            super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf input) throws Exception {
            Object decoded = super.decode(ctx, input);
            if (decoded instanceof ByteBuf) {
                ByteBuf frame = (ByteBuf) decoded;
                if (frame.readableBytes() >= TOTAL_LENGTH) {
                    try {
                        Channel ch = ctx.channel();
                        NettyChannel channel = NettyChannel.getOrAddChannel(ch, url);
                        return codec.decode(channel, frame);
                    } catch (Exception e) {
                        log.error("Decode frame error!", e);
                        throw e;
                    } finally {
                        frame.release();
                    }
                }
            }
            return decoded;
        }
    }

}
