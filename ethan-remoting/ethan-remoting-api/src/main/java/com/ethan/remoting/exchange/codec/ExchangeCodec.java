package com.ethan.remoting.exchange.codec;

import com.ethan.common.util.ByteUtils;
import com.ethan.remoting.Channel;
import com.ethan.remoting.Codec;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.exchange.support.DefaultFuture;
import com.ethan.remoting.transport.CodecSupport;
import com.ethan.serialize.Cleanable;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ethan.common.constant.CommonConstants.PROTOCOL_VERSION;
import static com.ethan.remoting.transport.CodecSupport.getSerialization;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class ExchangeCodec implements Codec {

    public static final String NAME = "exchange";
    // Header length
    public static final int HEADER_LENGTH = 16;
    // Magic header
    protected static final short MAGIC = (short) 0xdabb;
    protected static final byte MAGIC_HIGH = ByteUtils.short2bytes(MAGIC)[0];
    protected static final byte MAGIC_LOW = ByteUtils.short2bytes(MAGIC)[1];
    // Message flag
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWO_WAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;
    protected static final int SERIALIZATION_MASK = 0x1f;
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);


    @Override
    public void encode(Channel channel, ByteBuf buffer, Object message) throws IOException {
        if (message instanceof Request) {
            encodeRequest(channel, buffer, (Request) message);
        } else if (message instanceof Response) {
            encodeResponse(channel, buffer, (Response) message);
        }
    }

    @Override
    public Object decode(Channel url, ByteBuf buffer) throws Exception {
        int readable = buffer.readableBytes();
        byte[] header = new byte[Math.min(readable, HEADER_LENGTH)];
        buffer.readBytes(header);
        return decode(buffer, readable, header);
    }

    private Object decode(ByteBuf buffer, int readable, byte[] header) throws IOException {
        // Check magic number
        if (readable > 0 && header[0] != MAGIC_HIGH || readable > 1 && header[1] != MAGIC_LOW) {
            return DecodeResult.NEED_MAGIC_NUMBER;
        }
        // Check length
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // Get data length
        int len = ByteUtils.bytes2int(header, 12);

        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        ByteBufInputStream is = new ByteBufInputStream(buffer, len);

        return decodeBody(is, header);
    }

    protected Object decodeBody(InputStream is, byte[] header) throws IOException {
        byte flag = header[2], proto = (byte) (flag & SERIALIZATION_MASK);
        // get request id.
        long id = ByteUtils.bytes2long(header, 4);
        if ((flag & FLAG_REQUEST) == 0) {
            // Decode response
            Response res = new Response(id);
            // Get status
            byte status = header[3];
            res.setStatus(status);
            try {
                if (status == Response.OK) {
                    Object data;
                    data = decodeResponseData(
                            CodecSupport.deserialize(is, proto),
                            getRequestData(res, id));

                    res.setResult(data);
                } else {
                    res.setErrorMsg(CodecSupport.deserialize(is, proto)
                            .readUTF());
                }
            } catch (Throwable t) {
                res.setStatus(Response.CLIENT_ERROR);
                res.setErrorMsg(t.toString());
            }
            return res;
        } else {
            // Decode request
            Request req;
            try {
                Object data;
                req = new Request(id);
                data = decodeRequestData(CodecSupport.deserialize(is, proto));
                req.setData(data);
            } catch (Throwable t) {
                // Bad request
                req = new Request(id);
                req.setBroken(true);
                req.setData(t);
            }
            req.setVersion(PROTOCOL_VERSION);
            return req;
        }
    }

    protected Object decodeResponseData(ObjectInput in, Object requestData) throws IOException {
        return decodeResponseData(in);
    }

    protected Object decodeResponseData(ObjectInput in) throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.toString());
        }
    }


    protected Object decodeRequestData(ObjectInput in) throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.toString());
        }
    }

    protected Object getRequestData(Response response, long id) {
        DefaultFuture future = DefaultFuture.getFuture(id);
        if (future != null) {
            Request req = future.getRequest();
            if (req != null) {
                return req.getData();
            }
        }

        log.warn("The timeout response finally returned at {}, response status is {}, " +
                "response id is {}, please check provider side for detailed result.", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), response.getStatus(), response.getId());
        throw new IllegalArgumentException("Failed to find any request match the response, response id: " + id);
    }

    protected void encodeRequest(Channel channel, ByteBuf buffer, Request req) throws IOException {
        Serialization serialization = getSerialization(channel.getUrl());
        // Header
        byte[] header = new byte[HEADER_LENGTH];
        // Set magic number
        ByteUtils.short2bytes(MAGIC, header);

        // Set request and serialization flag
        header[2] = (byte) (FLAG_REQUEST | serialization.getContentTypeId());
        if (req.isEvent()) {
            header[2] |= FLAG_EVENT;
        }

        // Set request id
        ByteUtils.long2bytes(req.getId(), header, 4);

        // Encode request data
        int savedWriteIndex = buffer.writerIndex();
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
        ByteBufOutputStream bos = new ByteBufOutputStream(buffer);

        if (req.isHeartbeat()) {
            // Heartbeat request data is always null
            bos.write(CodecSupport.getNullBytesOf(serialization));
        } else {
            ObjectOutput out = serialization.serialize(bos);
            if (req.isEvent()) {
                encodeEventData(out, req.getData());
            } else {
                encodeRequestData(channel, out, req.getData(), req.getVersion());
            }
            out.flushBuffer();
            if (out instanceof Cleanable) {
                ((Cleanable) out).cleanup();
            }
        }

        bos.flush();
        bos.close();
        int len = bos.writtenBytes();
        ByteUtils.int2bytes(len, header, 12);

        // Write
        buffer.writerIndex(savedWriteIndex);
        // Write header
        buffer.writeBytes(header);
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
    }

    protected void encodeResponseData(Channel channel, ObjectOutput out, Object data) throws IOException {
        encodeResponseData(out, data);
    }

    protected void encodeRequestData(Channel channel, ObjectOutput out, Object data, String version)
            throws IOException {
        encodeRequestData(out, data);
    }

    protected void encodeRequestData(ObjectOutput out, Object data) throws IOException {
        out.writeObject(data);
    }

    protected void encodeResponseData(ObjectOutput out, Object data) throws IOException {
        out.writeObject(data);
    }


    protected void encodeResponse(Channel channel, ByteBuf buffer, Response res) throws IOException {
        int savedWriteIndex = buffer.writerIndex();
        try {
            Serialization serialization = getSerialization(channel.getUrl());
            // Header
            byte[] header = new byte[HEADER_LENGTH];
            // Set magic number
            ByteUtils.short2bytes(MAGIC, header);
            // set request and serialization flag.
            header[2] = serialization.getContentTypeId();
            if (res.isHeartbeat()) {
                header[2] |= FLAG_EVENT;
            }
            // Set response status
            byte status = res.getStatus();
            header[3] = status;
            // Set request id
            ByteUtils.long2bytes(res.getId(), header, 4);

            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
            ByteBufOutputStream bos = new ByteBufOutputStream(buffer);

            // Encode response data or error message.
            if (status == Response.OK) {
                if (res.isHeartbeat()) {
                    // heartbeat response data is always null
                    bos.write(CodecSupport.getNullBytesOf(serialization));
                } else {
                    ObjectOutput out = serialization.serialize(bos);
                    if (res.isEvent()) {
                        encodeEventData(out, res.getResult());
                    } else {
                        encodeResponseData(channel, out, res.getResult());
                    }
                    out.flushBuffer();
                    if (out instanceof Cleanable) {
                        ((Cleanable) out).cleanup();
                    }
                }
            } else {
                ObjectOutput out = serialization.serialize(bos);
                out.writeUTF(res.getErrorMsg());
                out.flushBuffer();
                if (out instanceof Cleanable) {
                    ((Cleanable) out).cleanup();
                }
            }

            bos.flush();
            bos.close();

            int len = bos.writtenBytes();
            ByteUtils.int2bytes(len, header, 12);
            // write
            buffer.writerIndex(savedWriteIndex);
            // Write header
            buffer.writeBytes(header);
            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
        } catch (Throwable t) {
            // clear buffer
            buffer.writerIndex(savedWriteIndex);
            // send error message to Consumer, otherwise, Consumer will wait till timeout.
            if (!res.isEvent() && res.getStatus() != Response.BAD_RESPONSE) {
                Response r = new Response(res.getId(), res.getVersion());
                r.setStatus(Response.BAD_RESPONSE);

                log.warn("Fail to encode response: {}, send bad_response info instead, cause: {}", res, t.getMessage(), t);
                try {
                    r.setErrorMsg("Failed to send response: " + res + ", cause: " + t.toString());
                    channel.send(r);
                    return;
                } catch (RemotingException e) {
                    log.warn("Failed to send bad_response info back: {}, cause: {}", res, e.getMessage(), e);
                }

            }

            // Rethrow exception
            if (t instanceof IOException) {
                throw (IOException) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new RuntimeException(t.getMessage(), t);
            }
        }
    }

    private void encodeEventData(ObjectOutput out, Object data) throws IOException {
        out.writeEvent((String) data);
    }

    protected void encodeRequestData(Channel channel, ObjectOutput out, Object data) throws IOException {
        encodeRequestData(channel, out, data, PROTOCOL_VERSION);
    }


}
