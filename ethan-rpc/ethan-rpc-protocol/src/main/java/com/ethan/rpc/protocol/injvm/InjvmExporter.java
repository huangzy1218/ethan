package com.ethan.rpc.protocol.injvm;

import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.protocol.AbstractExporter;

import java.util.Map;

/**
 * @author Huang Z.Y.
 */
public class InjvmExporter<T> extends AbstractExporter<T> {

    private final String key;

    private final Map<String, Exporter<?>> exporterMap;

    InjvmExporter(Invoker<T> invoker, Map<String, Exporter<?>> exporterMap) {
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
