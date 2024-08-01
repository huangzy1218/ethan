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

    NativeExporter(Invoker<T> invoker, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = invoker.getInterface().getName();
        this.exporterMap = exporterMap;
        exporterMap.put(key, this);
    }

    @Override
    public void afterUnExport() {
        exporterMap.remove(key);
    }

}
