package com.ethan.rpc.protocol.local;

import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.protocol.AbstractExporter;

import java.util.Map;

/**
 * @author Huang Z.Y.
 */
public class NativeExporter<T> extends AbstractExporter<T> {

    private final String key;

    private final Map<String, Exporter<?>> exporterMap;

    NativeExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
        exporterMap.put(key, this);
    }

    @Override
    public void afterUnExport() {
        exporterMap.remove(key);
    }

}
