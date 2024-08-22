package com.ethan.rpc.cluster.loadbalance;

import com.ethan.common.URL;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin loadbalancer.
 *
 * @author Huang Z.Y.
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 * <p>
 * 当前轮询的下标
 */
/**
 * 当前轮询的下标
 */

@Override
public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
    if (serviceMetaInfoList.isEmpty()) {
        return null;
    }
    // 只有一个服务，无需轮询
    int size = serviceMetaInfoList.size();
    if (size == 1) {
        return serviceMetaInfoList.get(0);
    }
    // 取模算法轮询
    int index = currentIndex.getAndIncrement() % size;
    return serviceMetaInfoList.get(index);
}

public class RoundRobinLoadBalancer extends AbstractLoadBalance {

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
        // 取模算法轮询
        int index = currentIndex.getAndIncrement() % size;
        return invokers.get(index);
    }

}
