package com.ethan.rpc.cluster.loadbalance;

import com.ethan.common.URL;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Huang Z.Y.
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "random";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int length = invokers.size();

        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }

}
