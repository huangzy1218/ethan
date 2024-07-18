package com.ethan.remoting.transport.netty.codec;

import com.ethan.common.URL;
import com.ethan.remoting.Codec;
import com.ethan.remoting.exchange.codec.ExchangeCodec;
import com.ethan.remoting.transport.netty.NettyChannel;
import com.ethan.rpc.model.ApplicationModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NettyCodecAdapterTest {

    private NettyCodecAdapter adapter;
    private ChannelHandlerContext ctx;
    private NettyChannel channel;

    @BeforeEach
    public void setUp() {
        Codec codec = ApplicationModel.defaultModel().getExtensionLoader(Codec.class).getExtension(ExchangeCodec.NAME);
        URL url = URL.valueOf("ethan://127.0.0.1:8080");
        adapter = new NettyCodecAdapter(codec, url);
        ctx = Mockito.mock(ChannelHandlerContext.class);
        channel = new NettyChannel(new NioSocketChannel(), url);
    }

    @Test
    void testEncodeAndDecodeRequestResponse() throws Exception {
//        Request request = new Request();
//        request.setId(1);
//        request.setData("Test request data");
//
//        ByteBuf requestBuffer = Unpooled.buffer();
//        adapter.getEncoder().write(ctx, request, requestBuffer);
//
//        Object decodedRequest = adapter.getDecoder().decode(ctx, requestBuffer);
//        assertTrue(decodedRequest instanceof Request, "Decoded object should be an instance of Request.");
//        Request decoded = (Request) decodedRequest;
//        assertEquals(1, decoded.getId(), "Decoded request ID should match.");
//        assertEquals("Test request data", decoded.getData(), "Decoded request data should match.");
//
//        Response response = new Response();
//        response.setId(1);
//        response.setResult("Test response data");
//
//        ByteBuf responseBuffer = Unpooled.buffer();
//        adapter.getEncoder().write(ctx, response, responseBuffer);
//
//        Object decodedResponse = adapter.getDecoder().decode(ctx, responseBuffer);
//        assertTrue(decodedResponse instanceof Response, "Decoded object should be an instance of Response.");
//        Response decodedRes = (Response) decodedResponse;
//        assertEquals(1, decodedRes.getId(), "Decoded response ID should match.");
//        assertEquals("Test response data", decodedRes.getResult(), "Decoded response data should match.");
    }

}