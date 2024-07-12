package com.ethan.rpc.protocol.codec;

import com.ethan.common.enumeration.CompressType;
import com.ethan.common.enumeration.SerializationType;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Huang Z.Y.
 */
public class ExchangeCodec implements Codec {

    protected static final CompressType DEFAULT_REMOTING_COMPRESS = CompressType.GZIP;
    protected static final SerializationType DEFAULT_REMOTING_SERIALIZATION = SerializationType.FASTJSON2;
    protected static final int HEADER_LENGTH = 16;
    protected static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    protected static final byte TOTAL_LENGTH = 16;
    protected static final int HEAD_LENGTH = 16;
    protected static final short MAGIC = (short) 0xffff;
    // Magic number to verify RpcMessage.
    protected static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    protected static final byte VERSION = 1;
    protected static final byte REQUEST_TYPE = 1;
    protected static final byte RESPONSE_TYPE = 2;
    // Ping.
    protected static final byte HEARTBEAT_REQUEST_TYPE = 3;
    // Pong.
    protected static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    protected static final String PING = "ping";
    protected static final String PONG = "pong";
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);


    @Override
    public void encode(ByteBuf buffer, Object message) throws IOException {
        if (message instanceof Request) {
            encodeRequest(buffer, (Request) message);
        } else if (message instanceof Response) {
            encodeResponse(buffer, (Response) message);
        }
    }

    @Override
    public Object decode(ByteBuf buffer) throws Exception {
        int readable = buffer.readableBytes();
        byte[] header = new byte[Math.min(readable, HEADER_LENGTH)];
        buffer.readBytes(header);
        return decode(buffer, readable, header);
    }

    private Object decode(ByteBuf buffer, int readable, byte[] header) throws IOException {
        // check magic number
        if (readable > 0 && header[0] != MAGIC_HIGH || readable > 1 && header[1] != MAGIC_LOW) {
            int length = header.length;
            if (header.length < readable) {
                header = Bytes.copyOf(header, readable);
                buffer.readBytes(header, length, readable - length);
            }
            for (int i = 1; i < header.length - 1; i++) {
                if (header[i] == MAGIC_HIGH && header[i + 1] == MAGIC_LOW) {
                    buffer.readerIndex(buffer.readerIndex() - header.length + i);
                    header = Bytes.copyOf(header, i);
                    break;
                }
            }
            return super.decode(channel, buffer, readable, header);
        }
        // Check length
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // Get data length
        int len = Bytes.bytes2int(header, 12);
        // When receiving response, how to exceed the length, then directly construct a response to the client.
        // see more detail from https://github.com/apache/dubbo/issues/7021.
        Object obj = finishRespWhenOverPayload(channel, len, header);
        if (null != obj) {
            return obj;
        }

        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        return decodeBody(buffer, header);
    }
}
