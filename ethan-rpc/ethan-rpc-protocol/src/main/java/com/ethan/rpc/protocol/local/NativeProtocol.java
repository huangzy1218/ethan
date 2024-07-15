package com.ethan.rpc.protocol.local;

import com.ethan.common.URL;
import com.ethan.common.util.CollectionUtils;
import com.ethan.common.util.UrlUtils;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.model.ApplicationModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ethan.common.constant.CommonConstants.LOCAL_PROTOCOL;

/**
 * @author Huang Z.Y.
 */
public class NativeProtocol implements Protocol {

    public static final String NAME = LOCAL_PROTOCOL;

    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

    public static NativeProtocol getNativeProtocol() {
        return (NativeProtocol) ApplicationModel.defaultModel()
                .getExtensionLoader(Protocol.class).getExtension(NativeProtocol.NAME);
    }

    static Exporter<?> getExporter(Map<String, Exporter<?>> map, URL key) {
        Exporter<?> result = null;

        if (!key.getServiceKey().contains("*")) {
            result = map.get(key.getServiceKey());
        } else {
            if (CollectionUtils.isNotEmptyMap(map)) {
                for (Exporter<?> exporter : map.values()) {
                    if (UrlUtils.isServiceKeyMatch(key, exporter.getInvoker().getUrl())) {
                        result = exporter;
                        break;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return new NativeExporter<>(invoker, invoker.getUrl().getServiceKey(), exporterMap);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return new NativeInvoker<>(type, url, url.getServiceKey(), exporterMap);
    }

}
