package com.ethan.rpc.protocol.injvm;

import com.ethan.common.URL;
import com.ethan.model.ApplicationModel;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.RpcException;

/**
 * @author Huang Z.Y.
 */
public class InjvmProtocol implements Protocol {

    public static final String NAME = "injvm";

    protected final Exporter<?> exporter = null;

    public static InjvmProtocol getInjvmProtocol() {
        return (InjvmProtocol) ApplicationModel.defaultModel()
                .getExtensionLoader(Protocol.class).getExtension(InjvmProtocol.NAME);
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return new InjvmExporter<>(invoker);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url, Exporter<T> exporter) throws RpcException {
        return new InjvmInvoker<>(type, url, exporter);
    }

}
