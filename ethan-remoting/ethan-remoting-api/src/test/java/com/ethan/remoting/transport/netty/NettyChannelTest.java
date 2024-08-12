package com.ethan.remoting.transport.netty;

import com.ethan.common.RemotingException;
import com.ethan.common.URL;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NettyChannelTest {

    @Test
    void test() throws RemotingException {
        Channel ch = Mockito.mock(Channel.class);
        Mockito.when(ch.isActive()).thenReturn(true);
        URL url = URL.valueOf("test://127.0.0.1/test");
        NettyChannel channel = NettyChannel.getOrAddChannel(ch, url);
        channel.send("Hello World");
    }

}