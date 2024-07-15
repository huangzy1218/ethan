package com.ethan.rpc.protocol;

import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;

/**
 * Abstract exporter.
 *
 * @author Huang Z.Y.
 */
public abstract class AbstractExporter<T> implements Exporter<T> {

    private final Invoker<T> invoker;
    private volatile boolean unexported = false;

    public AbstractExporter(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public final void unexport() {
        if (unexported) {
            return;
        }
        unexported = true;
        afterUnExport();
    }

    public void afterUnExport() {
    }
    
}
