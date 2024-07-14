package com.ethan.rpc.protocol;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingException;
import com.ethan.rpc.*;
import com.ethan.rpc.support.RpcUtils;
import com.ethan.serialize.SerializationException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.ethan.common.constant.CommonConstants.TIMEOUT_KEY;

/**
 * This Invoker works on Consumer side.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public abstract class AbstractInvoker<T> implements Invoker<T> {

    /**
     * Service interface type.
     */
    private final Class<T> type;

    /**
     * Url.
     */
    private final URL url;

    public AbstractInvoker(Class<T> type, URL url) {
        this.type = type;
        this.url = url;
    }


    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation inv) throws RpcException {
        RpcInvocation invocation = (RpcInvocation) inv;

        // Do invoke rpc invocation and return async result
        AsyncRpcResult asyncResult = doInvokeAndReturn(invocation);

        waitForResultIfSync(asyncResult, invocation);
        return asyncResult;
    }


    private AsyncRpcResult doInvokeAndReturn(RpcInvocation invocation) {
        AsyncRpcResult asyncResult;
        try {
            asyncResult = (AsyncRpcResult) doInvoke(invocation);
        } catch (InvocationTargetException e) {
            Throwable te = e.getTargetException();
            if (te != null) {
                // If biz exception
                if (te instanceof RpcException) {
                    ((RpcException) te).setCode(RpcException.BIZ_EXCEPTION);
                }
                asyncResult = AsyncRpcResult.newDefaultAsyncResult(null, te, invocation);
            } else {
                asyncResult = AsyncRpcResult.newDefaultAsyncResult(null, e, invocation);
            }
        } catch (RpcException e) {
            // If biz exception
            if (e.isBiz()) {
                asyncResult = AsyncRpcResult.newDefaultAsyncResult(null, e, invocation);
            } else {
                throw e;
            }
        } catch (Throwable e) {
            asyncResult = AsyncRpcResult.newDefaultAsyncResult(null, e, invocation);
        }

        return asyncResult;
    }

    private void waitForResultIfSync(AsyncRpcResult asyncResult, RpcInvocation invocation) {
        if (InvokeMode.SYNC != invocation.getInvokeMode()) {
            return;
        }
        try {
            Object timeoutKey = invocation.get(TIMEOUT_KEY);
            long timeout = RpcUtils.convertToNumber(timeoutKey, Integer.MAX_VALUE);

            asyncResult.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RpcException(
                    "Interrupted unexpectedly while waiting for remote result to return! method: "
                            + invocation.getMethodName() + ", provider: " + getUrl() + ", cause: " + e.getMessage(),
                    e);
        } catch (ExecutionException e) {
            Throwable rootCause = e.getCause();
            if (rootCause instanceof TimeoutException) {
                throw new RpcException("Invoke remote method timeout. method: " + invocation.getMethodName() + ", provider: "
                        + getUrl() + ", cause: " + e.getMessage(), e);
            } else if (rootCause instanceof RemotingException) {
                throw new RpcException("Failed to invoke remote method: " + invocation.getMethodName() + ", provider: " + getUrl()
                        + ", cause: " + e.getMessage(), e);
            } else if (rootCause instanceof SerializationException) {
                throw new RpcException("Invoke remote method failed cause by serialization error.  remote method: "
                        + invocation.getMethodName() + ", provider: " + getUrl() + ", cause: " + e.getMessage(), e);
            } else {
                throw new RpcException("Fail to invoke remote method: " + invocation.getMethodName() + ", provider: " + getUrl()
                        + ", cause: " + e.getMessage(), e);
            }
        } catch (java.util.concurrent.TimeoutException e) {
            throw new RpcException("Invoke remote method timeout. method: " + invocation.getMethodName() + ", provider: " + getUrl()
                    + ", cause: " + e.getMessage(), e);
        } catch (Throwable e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

    protected abstract Result doInvoke(Invocation invocation) throws Throwable;

}
    