package com.ethan.remoting.exchange.codec;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.transport.netty.NettyChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.ethan.common.util.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ExchangeCodecTest {

    private ExchangeCodec codec;
    private ChannelHandlerContext ctx;
    private Channel channel;

    @BeforeEach
    public void setUp() {
        codec = new ExchangeCodec();
        ctx = Mockito.mock(ChannelHandlerContext.class);
        channel = new NettyChannel(new NioSocketChannel(), URL.valueOf("http://127.0.0.1:8080"));
    }

    @Test
    void testEncodeRequest() throws Exception {
        Request request = new Request();
        request.setData("Test request data");

        ByteBuf buffer = Unpooled.buffer();
        codec.encode(channel, buffer, request);

        assertTrue(buffer.isReadable(), "Buffer should be readable after encoding.");
    }

    @Test
    void testDecodeRequest() throws Exception {
        // Simulate a request byte array
        ByteBuf buffer = Unpooled.buffer();
        Request request = new Request();
        request.setData("Hello World");

        // Encode the request into the buffer
        codec.encode(channel, buffer, request);

        // Decode the request
        Object decodedRequest = codec.decode(channel, buffer);

        assertTrue(decodedRequest instanceof Request, "Decoded object should be an instance of Request.");
        Request decoded = (Request) decodedRequest;
        assertEquals("Hello World", decoded.getData());
    }

    @Test
    void testEncodeResponse() throws Exception {
        Response response = new Response();
        response.setResult("Test response data");

        ByteBuf buffer = Unpooled.buffer();
        codec.encode(channel, buffer, response);

        assertTrue(buffer.isReadable(), "Buffer should be readable after encoding.");
    }

    @Test
    void testDecodeResponse() throws Exception {
        // Simulate a response byte array
        ByteBuf buffer = Unpooled.buffer();
        Response response = new Response();
        response.setResult("Hello World");
        // Encode the response into the buffer
        codec.encode(channel, buffer, response);

        // Decode the response
        Object decodedResponse = codec.decode(channel, buffer);

        assertTrue(decodedResponse instanceof Response, "Decoded object should be an instance of Response.");
        Response decoded = (Response) decodedResponse;
        assertEquals("Hello World", decoded.getResult());
    }

    @Test
    void testEncodeAndDecodeRequestResponse() throws Exception {
        // Create request
        Request request = new Request();
        request.setData("Hello World");

        // Encode request
        ByteBuf requestBuffer = Unpooled.buffer();
        codec.encode(channel, requestBuffer, request);

        // Decode request
        Object decodedRequest = codec.decode(channel, requestBuffer);
        assertTrue(decodedRequest instanceof Request, "Decoded object should be an instance of Request.");
        Request decoded = (Request) decodedRequest;
        System.out.println("Request: " + decoded.getData());
        assertEquals("Hello World", decoded.getData());

        Response response = new Response();
        response.setId(request.getId());
        response.setResult("Hello World");

        // Encode response
        ByteBuf responseBuffer = Unpooled.buffer();
        codec.encode(channel, responseBuffer, response);

        // Decode response
        Object decodedResponse = codec.decode(channel, responseBuffer);
        assertTrue(decodedResponse instanceof Response, "Decoded object should be an instance of Response.");
        Response decodedRes = (Response) decodedResponse;
        System.out.println("Response: " + decodedRes.getResult());
        assertEquals("Hello World", decodedRes.getResult());
    }

}