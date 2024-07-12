package com.ethan.rpc.protocol.codec;

import com.ethan.common.util.ByteUtils;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Huang Z.Y.
 */
public class ExchangeCodec implements Codec {

    // Header length
    protected static final int HEADER_LENGTH = 16;
    // Magic header
    protected static final short MAGIC = (short) 0xdabb;
    protected static final byte MAGIC_HIGH = ByteUtils.short2bytes(MAGIC)[0];
    protected static final byte MAGIC_LOW = ByteUtils.short2bytes(MAGIC)[1];
    // Message flag
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;
    protected static final int SERIALIZATION_MASK = 0x1f;
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

        return decodeBody(buffer, header);
    }

    protected Object decodeBody(ByteBuf is, byte[] header) throws IOException {
        byte flag = header[2], proto = (byte) (flag & SERIALIZATION_MASK);
        // get request id.
        long id = ByteUtils.bytes2long(header, 4);
        if ((flag & FLAG_REQUEST) == 0) {
            // decode response.
            Response res = new Response(id);
            if ((flag & FLAG_EVENT) != 0) {
                res.setEvent(true);
            }
            // get status.
            byte status = header[3];
            res.setStatus(status);
            try {
                if (status == Response.OK) {
                    Object data;
                    if (res.isEvent()) {
                        byte[] eventPayload = CodecSupport.getPayload(is);
                        if (CodecSupport.isHeartBeat(eventPayload, proto)) {
                            // heart beat response data is always null;
                            data = null;
                        } else {
                            data = decodeEventData(
                                    channel,
                                    CodecSupport.deserialize(
                                            channel.getUrl(), new ByteArrayInputStream(eventPayload), proto),
                                    eventPayload);
                        }
                    } else {
                        data = decodeResponseData(
                                channel,
                                CodecSupport.deserialize(channel.getUrl(), is, proto),
                                getRequestData(channel, res, id));
                    }
                    res.setResult(data);
                } else {
                    res.setErrorMessage(CodecSupport.deserialize(channel.getUrl(), is, proto)
                            .readUTF());
                }
            } catch (Throwable t) {
                res.setStatus(Response.CLIENT_ERROR);
                res.setErrorMessage(StringUtils.toString(t));
            }
            return res;
        } else {
            // decode request.
            Request req;
            try {
                Object data;
                if ((flag & FLAG_EVENT) != 0) {
                    byte[] eventPayload = CodecSupport.getPayload(is);
                    if (CodecSupport.isHeartBeat(eventPayload, proto)) {
                        // heart beat response data is always null;
                        req = new HeartBeatRequest(id);
                        ((HeartBeatRequest) req).setProto(proto);
                        data = null;
                    } else {
                        req = new Request(id);
                        data = decodeEventData(
                                channel,
                                CodecSupport.deserialize(
                                        channel.getUrl(), new ByteArrayInputStream(eventPayload), proto),
                                eventPayload);
                    }
                    req.setEvent(true);
                } else {
                    req = new Request(id);
                    data = decodeRequestData(channel, CodecSupport.deserialize(channel.getUrl(), is, proto));
                }
                req.setData(data);
            } catch (Throwable t) {
                // bad request
                req = new Request(id);
                req.setBroken(true);
                req.setData(t);
            }
            req.setVersion(Version.getProtocolVersion());
            req.setTwoWay((flag & FLAG_TWOWAY) != 0);
            return req;
        }
    }
}
