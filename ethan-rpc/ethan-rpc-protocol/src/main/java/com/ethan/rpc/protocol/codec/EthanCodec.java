package com.ethan.rpc.protocol.codec;

import com.ethan.common.enumeration.CompressType;
import com.ethan.common.enumeration.SerializationType;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.rpc.AppResponse;
import com.ethan.rpc.CodecSupport;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Message;
import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The ethan implement of {@link Codec}.
 *
 * @author Huang Z,Y.
 */
@Slf4j
public class EthanCodec implements Codec {

    protected static final CompressType DEFAULT_REMOTING_COMPRESS = CompressType.GZIP;
    protected static final SerializationType DEFAULT_REMOTING_SERIALIZATION = SerializationType.FASTJSON2;
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

    public Short getMagicCode() {
        return MAGIC;
    }

    @Override
    public void encode(ByteBuf out, Object msg) throws IOException {
        if (msg instanceof Request) {
            encodeRequest(channel, buffer, (Request) msg);
        } else if (msg instanceof Response) {
            encodeResponse(channel, buffer, (Response) msg);
        }
    }

    @Override
    public Object decode(ByteBuf in) throws Exception {
        // Must read ByteBuf in order
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        // Build RpcMessage object
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        Message message = Message.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == HEARTBEAT_REQUEST_TYPE) {
            message.setData(PING);
            return message;
        }
        if (messageType == HEARTBEAT_RESPONSE_TYPE) {
            message.setData(PONG);
            return message;
        }
        int bodyLength = fullLength - HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // Decompress the bytes
            Compressor compress = CodecSupport.getCompressor(compressType);
            bs = compress.decompress(bs);
            // Deserialize the object
            Serialization serializer = CodecSupport.getSerialization(codecType);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bs);
            ObjectInput objectInput = serializer.deserialize(inputStream);
            if (messageType == REQUEST_TYPE) {
                Invocation invocation = objectInput.readObject(Invocation.class);
                message.setData(invocation);
            } else {
                AppResponse result = objectInput.readObject(AppResponse.class);
                message.setData(result);
            }
        }
        return message;

    }

    private void checkVersion(ByteBuf in) {
        // Read the version and compare
        byte version = in.readByte();
        if (version != VERSION) {
            throw new RuntimeException("Version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // Read the first 4 bit, which is the magic number, and compare
        int len = MAGIC_NUMBER.length;
        byte[] bs = new byte[len];
        in.readBytes(bs);
        for (int i = 0; i < len; i++) {
            if (bs[i] != MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(bs));
            }
        }
    }

}
