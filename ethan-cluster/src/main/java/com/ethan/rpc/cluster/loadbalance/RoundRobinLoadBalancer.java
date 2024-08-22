package com.ethan.rpc.cluster.loadbalance;

import com.ethan.common.URL;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin loadbalancer.
 *
 * @author Huang Z.Y.
 */

public class RoundRobinLoadBalancer extends AbstractLoadBalance {

    public static final String NAME = "roundrobin";

    /**
     * The index of the current poll.
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        if (invokers.isEmpty()) {
            return null;
        }
        // Only one service, no need to poll
        int size = invokers.size();
        if (size == 1) {
            return invokers.get(0);
        }
        // Modulo algorithm polling
        int index = currentIndex.getAndIncrement() % size;
        return invokers.get(index);
    }

}
