package com.ethan.rpc.protocol.local;

import com.ethan.common.URL;
import com.ethan.rpc.*;
import com.ethan.rpc.protocol.injvm.InjvmInvoker;
import com.example.DemoService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class InjvmInvokerTest {

    Class<?>[] types = new Class[]{String.class};
    Object[] args = new Object[]{(String) "Huang Z.Y."};
    Invocation invocation = new RpcInvocation("sayHello", DemoService.class.getName(),
            "default/DemoService:1.0", types, args);

    private InjvmInvoker<DemoService> invoker;
    private Exporter<DemoService> exporter;
    private URL url;

    @BeforeEach
    public void setUp() {
//        url = URL.valueOf("ethan://localhost:10010/DemoService");
//        // Create the exporter and set the invoker
//        // Create the invoker first
//        invoker = new InjvmInvoker<>(DemoService.class, url, url.getServiceKey(), exporter);
//        exporter = new InjvmExporter<>(invoker, null, new HashMap<>());
//        invoker.setExporter(exporter);
////        when(exporter.getInvoker()).thenReturn(invoker);
    }

    @Test
    public void testDoInvokeSyncSuccess() throws Throwable {
        Result mockResult = new AppResponse("Success");
        Result result = invoker.doInvoke(invocation);
//        assertNotNull(result);
//        assertEquals("Success", result.getValue());
    }

    @Test
    public void testDoInvokeSyncWithException() throws Throwable {
        Result mockResult = new AppResponse(new RuntimeException("Error"));
        when(invoker.invoke(invocation)).thenReturn(mockResult);
        Result result = invoker.doInvoke(invocation);

        assertNotNull(result);
        assertTrue(result.hasException());
        assertEquals("Error", result.getException().getMessage());
    }

    @Test
    public void testDoInvokeAsyncSuccess() throws Throwable {
        Result mockResult = new AppResponse("Async Success");

        when(invoker.invoke(invocation)).thenReturn(mockResult);
        when(invoker.getUrl()).thenReturn(url);

        // Simulate async behavior
        CompletableFuture<Result> future = CompletableFuture.completedFuture(mockResult);
        when(invoker.invoke(invocation)).thenReturn(mockResult);

        Result result = invoker.doInvoke(invocation);

        assertNotNull(result);
        assertEquals("Async Success", result.getValue());
    }

}