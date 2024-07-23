package com.ethan.rpc.cluster;

import com.ethan.common.URL;
import com.ethan.common.extension.SPI;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.RpcException;

import java.util.List;

/**
 * LoadBalance (SPI, Singleton, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {

    /**
     * Select one invoker in list.
     *
     * @param invokers   Invokers
     * @param url        Refer url
     * @param invocation Invocation
     * @return Selected invoker
     */
    <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;

}
