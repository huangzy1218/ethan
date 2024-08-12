package com.ethan.remoting.transport.netty;

import com.ethan.common.RemotingException;
import com.ethan.common.URL;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.RemotingServer;
import com.ethan.remoting.Transporter;
import com.ethan.remoting.transport.netty.client.NettyClient;
import com.ethan.remoting.transport.netty.server.NettyServer;

/**
 * @author Huang Z.Y.
 */
public class NettyTransporter implements Transporter {

    @Override
    public RemotingServer bind(URL url) throws RemotingException {
        return new NettyServer(url);
    }

    @Override
    public RemotingClient connect(URL url) throws RemotingException {
        return new NettyClient(url);
    }

}
