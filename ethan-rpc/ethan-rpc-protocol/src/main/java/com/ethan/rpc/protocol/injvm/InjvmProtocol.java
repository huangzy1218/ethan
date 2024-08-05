package com.ethan.rpc.protocol.injvm;

import com.ethan.model.ApplicationModel;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ethan.common.constant.CommonConstants.LOCAL_PROTOCOL;

/**
 * @author Huang Z.Y.
 */
public class InjvmProtocol implements Protocol {

    public static final String NAME = LOCAL_PROTOCOL;

    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

    public static InjvmProtocol getInjvmProtocol() {
        return (InjvmProtocol) ApplicationModel.defaultModel()
                .getExtensionLoader(Protocol.class).getExtension(NativeProtocol.NAME);
    }

    static Exporter<?> getExporter(Map<String, Exporter<?>> map, String key) {
        return map.get(key);
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return new InjvmExporter<>(invoker, exporterMap);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RpcException {
        return new InjvmInvoker<>(type, exporterMap);
    }

}
