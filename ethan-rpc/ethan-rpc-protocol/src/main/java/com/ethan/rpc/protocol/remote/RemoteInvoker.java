package com.ethan.rpc.protocol.remote;

import com.ethan.common.URL;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Result;
import com.ethan.rpc.protocol.AbstractInvoker;

/**
 * @author Huang Z.Y.
 */
public class RemoteInvoker<T> extends AbstractInvoker<T> {

    public RemoteInvoker(Class<T> type, URL url) {
        super(type, url);
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws Throwable {
        return null;
    }
    
}
