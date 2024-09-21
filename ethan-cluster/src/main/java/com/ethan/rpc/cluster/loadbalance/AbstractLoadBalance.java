package com.ethan.rpc.cluster.loadbalance;

import com.ethan.common.URL;
import com.ethan.common.util.CollectionUtils;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.cluster.LoadBalance;

import java.util.List;

/**
 * @author Huang Z.Y.
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invokers, url, invocation);
    }

    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation);

}
