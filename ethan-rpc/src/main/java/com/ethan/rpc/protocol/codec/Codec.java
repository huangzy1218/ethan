package com.ethan.rpc.protocol.codec;

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

    void encode(ByteBuf buffer, Object message) throws IOException;

    Object decode(ByteBuf buffer) throws Exception;

}
