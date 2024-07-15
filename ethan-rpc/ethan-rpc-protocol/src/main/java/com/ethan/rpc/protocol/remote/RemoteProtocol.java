package com.ethan.rpc.protocol.remote;

import com.ethan.common.URL;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.RpcException;

/**
 * @author Huang Z.Y.
 */
public class RemoteProtocol implements Protocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return null;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return null;
    }
    
}
