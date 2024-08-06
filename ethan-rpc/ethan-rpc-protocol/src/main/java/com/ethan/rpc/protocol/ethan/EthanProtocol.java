package com.ethan.rpc.protocol.ethan;

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
public class EthanProtocol implements Protocol {


    public static final String NAME = "ethan";

    public static final int DEFAULT_PORT = 8801;

    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        URL url = invoker.getUrl();
        String key = url.getServiceKey();
        return new EthanExporter<>(invoker);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url, Exporter<T> exporter) throws RpcException {
        return null;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return protocolBindingRefer(type);
    }

    public <T> Invoker<T> protocolBindingRefer(Class<T> serviceType) throws RpcException {
        // Create rpc invoker
        return new EthanInvoker<>(serviceType, URL.builder().build());
    }

}
