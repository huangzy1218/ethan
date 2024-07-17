package com.ethan.rpc.protocol.remote;

import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.protocol.AbstractExporter;

import java.util.Map;

/**
 * Ethan remote exporter.
 *
 * @author Huang Z.Y.
 */
public class RemoteExporter<T> extends AbstractExporter<T> {

    private final String key;

    private final Map<String, Exporter<?>> exporterMap;

    public RemoteExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
        exporterMap.put(key, this);
    }

    @Override
    public void afterUnExport() {
        exporterMap.remove(key, this);
    }

}
