package com.ethan.remoting.transport.netty.codec;

import com.ethan.common.URL;
import com.ethan.rpc.Message;
import com.ethan.rpc.protocol.codec.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

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
            codec.encode(out, msg);
        }
    }

    private class InternalDecoder extends LengthFieldBasedFrameDecoder {

        public InternalDecoder() {
            this(MAX_FRAME_LENGTH, 5, 4, -9, 0);
        }

        public InternalDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                               int lengthAdjustment, int initialBytesToStrip) {
            super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf input) throws Exception {
            Object decoded = super.decode(ctx, input);
            if (decoded instanceof ByteBuf frame) {
                if (frame.readableBytes() >= TOTAL_LENGTH) {
                    try {
                        return codec.decode(frame);
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
