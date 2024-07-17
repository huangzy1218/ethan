package com.ethan.remoting;

import com.ethan.common.extension.SPI;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Encapsulate constants and encode and decode methods.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface Codec {

    Integer MAX_FRAME_LENGTH = 8388608;

    void encode(Channel channel, ByteBuf buffer, Object message) throws IOException;

    Object decode(Channel channel, ByteBuf buffer) throws Exception;

    enum DecodeResult {
        NEED_MAGIC_NUMBER,
        NEED_MORE_INPUT,
        SKIP_SOME_INPUT
    }

}
