package com.ethan.remoting;

import com.ethan.common.URL;

/**
 * @author Huang Z.Y.
 */
public interface Transporter {

    /**
     * Bind a server.
     *
     * @param url Server url
     * @return Server
     * @throws RemotingException
     */
    RemotingServer bind(URL url) throws RemotingException;

    /**
     * Connect to a server.
     *
     * @param url Server url
     * @return client
     * @throws RemotingException
     */
    RemotingClient connect(URL url) throws RemotingException;

}
