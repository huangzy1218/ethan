package com.ethan.rpc.protocol.remote;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.TimeoutException;
import com.ethan.remoting.exchange.ExchangeClient;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.support.MessageExchangeClient;
import com.ethan.rpc.*;
import com.ethan.rpc.protocol.AbstractInvoker;
import com.ethan.rpc.support.RpcUtils;
import com.ethan.serialize.SerializationException;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

import static com.ethan.common.constant.CommonConstants.DEFAULT_TIMEOUT;
import static com.ethan.common.constant.CommonConstants.TIMEOUT_KEY;

/**
 * @author Huang Z.Y.
 */
public class RemoteInvoker<T> extends AbstractInvoker<T> {

    private ExchangeClient client;

    public RemoteInvoker(Class<T> type, URL url) {
        super(type, url);
        client = new MessageExchangeClient(new NioSocketChannel(), url);
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws Throwable {
        RpcInvocation inv = (RpcInvocation) invocation;
        final String methodName = RpcUtils.getMethodName(invocation);

        try {
            int timeout = RpcUtils.calculateTimeout(getUrl(), DEFAULT_TIMEOUT);
            if (timeout <= 0) {
                return AsyncRpcResult.newDefaultAsyncResult(
                        new RpcException(
                                "No time left for making the following call: " + invocation.getServiceName() + "."
                                        + RpcUtils.getMethodName(invocation) + ", terminate directly."),
                        invocation);
            }

            invocation.setAttachment(TIMEOUT_KEY, String.valueOf(timeout));

            Request request = new Request();
            request.setData(inv);

            client.send(request);
            return AsyncRpcResult.newDefaultAsyncResult(invocation);
        } catch (TimeoutException e) {
            throw new RpcException(
                    "Invoke remote method timeout. method: " + RpcUtils.getMethodName(invocation) + ", provider: "
                            + getUrl() + ", cause: " + e.getMessage(),
                    e);
        } catch (RemotingException e) {
            String remoteExpMsg = "Failed to invoke remote method: " + RpcUtils.getMethodName(invocation)
                    + ", provider: " + getUrl() + ", cause: " + e.getMessage();
            if (e.getCause() instanceof IOException && e.getCause().getCause() instanceof SerializationException) {
                throw new RpcException(remoteExpMsg, e);
            } else {
                throw new RpcException(remoteExpMsg, e);
            }
        }
    }

}
