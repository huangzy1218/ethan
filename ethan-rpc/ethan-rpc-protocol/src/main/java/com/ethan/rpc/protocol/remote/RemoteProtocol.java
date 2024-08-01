package com.ethan.rpc.protocol.remote;

import com.ethan.common.URL;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang Z.Y.
 */
public class RemoteProtocol implements Protocol {


    public static final String NAME = "dubbo";

    public static final int DEFAULT_PORT = 8801;

    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();


    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        URL url = invoker.getUrl();
        String key = url.getServiceKey();
        RemoteExporter<T> exporter = new RemoteExporter<>(invoker, key, exporterMap);
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RpcException {
        return protocolBindingRefer(type);
    }

    public <T> Invoker<T> protocolBindingRefer(Class<T> serviceType) throws RpcException {
        // Create rpc invoker
        RemoteInvoker<T> invoker = new RemoteInvoker<>(serviceType);
        return invoker;
    }

}
