package com.ethan.rpc.protocol.injvm;

import com.ethan.rpc.Invoker;
import com.ethan.rpc.protocol.AbstractExporter;

/**
 * @author Huang Z.Y.
 */
public class InjvmExporter<T> extends AbstractExporter<T> {

    InjvmExporter(Invoker<T> invoker) {
        super(invoker);
    }

}
