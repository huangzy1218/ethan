package com.ethan.rpc.protocol.ethan;

import com.ethan.rpc.Invoker;
import com.ethan.rpc.protocol.AbstractExporter;

/**
 * Ethan remote exporter.
 *
 * @author Huang Z.Y.
 */
public class EthanExporter<T> extends AbstractExporter<T> {

    public EthanExporter(Invoker<T> invoker) {
        super(invoker);
    }

}
