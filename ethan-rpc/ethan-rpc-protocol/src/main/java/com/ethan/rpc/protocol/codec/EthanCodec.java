package com.ethan.rpc.protocol.codec;

import com.ethan.rpc.AppResponse;
import com.ethan.rpc.CodecSupport;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Message;
import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ethan.rpc.Constants.*;


/**
 * The ethan implement of {@link Codec}.
 *
 * @author Huang Z,Y.
 */
@Slf4j
public class EthanCodec implements Codec {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    public void encode(ByteBuf out, Object msg) throws IOException {
        try {
            Message message = (Message) msg;
            out.writeBytes(MAGIC_NUMBER);
            out.writeByte(VERSION);
            // Leave a place to write the value of full length
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = message.getMessageType();
            out.writeByte(messageType);
            out.writeByte(message.getCodec());
            out.writeByte(message.getCompress());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // Build full length
            byte[] bodyBytes = null;
            int fullLength = HEAD_LENGTH;
            // If messageType is not heartbeat message, fullLength = head length + body length
            if (messageType != HEARTBEAT_REQUEST_TYPE
                    && messageType != HEARTBEAT_RESPONSE_TYPE) {
                // Serialize the object
                Serialization serializer = CodecSupport.getSerialization(message.getCodec());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutput objectOutput = serializer.serialize(outputStream);
                objectOutput.writeObject(message.getData());
                objectOutput.flushBuffer();
                bodyBytes = outputStream.toByteArray();
                // Compress the bytes
                Compressor compressor = CodecSupport.getCompressor(message.getCompress());
                bodyBytes = compressor.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
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
            throw new RuntimeException("version isn't compatible" + version);
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
