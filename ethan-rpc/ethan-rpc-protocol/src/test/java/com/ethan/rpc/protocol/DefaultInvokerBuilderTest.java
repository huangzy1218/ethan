package com.ethan.rpc.protocol;

import com.ethan.common.URL;
import com.ethan.rpc.*;
import com.example.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.ethan.common.constant.CommonConstants.INTERFACE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Huang Z.Y.
 */
class DefaultInvokerBuilderTest {

    private URL url = URL.valueOf("ethan://127.0.0.1/DemoService").addParameter(INTERFACE_KEY, DemoService.class.getName());

    // Mocked Invocation
    private RpcInvocation invocation;
    // Test class extending AbstractInvoker for testing purposes
    private AbstractInvoker<DemoService> invoker = new AbstractInvoker<>(DemoService.class, url) {
        @Override
        protected Result doInvoke(Invocation invocation) throws Throwable {
            // Simulate some invocation logic
            if ("sayHello".equals(invocation.getMethodName())) {
                // Simulate synchronous call
                if (InvokeMode.SYNC == ((RpcInvocation) invocation).getInvokeMode()) {
                    // Simulate processing time
                    Thread.sleep(100);
                    return AsyncRpcResult.newDefaultAsyncResult("Sync response", null, (RpcInvocation) invocation);
                } else {
                    AsyncRpcResult result = new AsyncRpcResult(new CompletableFuture<>(), invocation);
                    new Thread(() -> {
                        try {
                            Thread.sleep(100); // Simulate processing time
                            result.setValue("Async response");
                            // You can set other attributes of result as needed
                        } catch (InterruptedException e) {
                            result.setException(e);
                        }
                    }).start();
                    return result;
                }
            }
            return null;
        }
    };

    @BeforeEach
    void setUp() {
        Class<?>[] types = new Class[]{String.class};
        Object[] args = new Object[]{(String) "Huang Z.Y."};
        invocation = new RpcInvocation("sayHello", DemoService.class.getName(),
                "default/DemoService:1.0", types, args);
    }

    @Test
    void testSyncInvoke() {
        // Prepare invocation for synchronous call
        invocation.setInvokeMode(InvokeMode.SYNC);

        try {
            Result result = invoker.invoke(invocation);
            assertEquals("Sync response", result.getValue());
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAsyncInvoke() {
        // Prepare invocation for asynchronous call
        invocation.setInvokeMode(InvokeMode.ASYNC);

        try {
            Result result = invoker.invoke(invocation);
            assertEquals("Async response", result.get(1000, TimeUnit.MILLISECONDS).getValue());
        } catch (RpcException | InterruptedException | java.util.concurrent.TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}


