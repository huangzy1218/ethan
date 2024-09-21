package com.ethan.remoting.transport.netty.codec;

import com.ethan.common.URL;
import com.ethan.remoting.Codec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class NettyCodecAdapterTest {

    private NettyCodecAdapter codecAdapter;
    private Codec codec;
    private URL url;

    @BeforeEach
    public void setUp() {
        // Mock the dependencies
        codec = mock(Codec.class);
        url = mock(URL.class);

        // Initialize the codec adapter with mocks
        codecAdapter = new NettyCodecAdapter(codec, url);
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