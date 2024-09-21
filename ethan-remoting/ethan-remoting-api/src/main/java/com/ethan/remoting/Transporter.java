package com.ethan.remoting;

import com.ethan.common.RemotingException;
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
     * @throws com.ethan.common.RemotingException
     */
    RemotingServer bind(URL url) throws com.ethan.common.RemotingException;

    /**
     * Connect to a server.
     *
     * @param url Server url
     * @return client
     * @throws com.ethan.common.RemotingException
     */
    RemotingClient connect(URL url) throws RemotingException;

}
