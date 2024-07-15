package com.ethan.rpc.protocol.local;

import com.ethan.common.URL;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.model.ApplicationModel;
import com.example.DemoService;
import com.example.DemoServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ethan.common.constant.CommonConstants.*;

public class NativeProtocolTest {
    private final Protocol protocol = ApplicationModel.defaultModel()
            .getExtensionLoader(Protocol.class).getExtension(LOCAL_PROTOCOL);
    private final ProxyFactory proxy = ApplicationModel.defaultModel()
            .getExtensionLoader(ProxyFactory.class).getExtension(DEFAULT_PROXY);
    private final List<Exporter<?>> exporters = new ArrayList<>();

    @Test
    void testLocalProtocol() throws Exception {
        DemoService service = new DemoServiceImpl();
        Invoker<?> invoker = proxy.getInvoker(
                service, DemoService.class,
                URL.valueOf("native://127.0.0.1:8080/DemoService")
                        .addParameter(INTERFACE_KEY, DemoService.class.getName()));
        Exporter<?> exporter = protocol.export(invoker);
        exporters.add(exporter);
        Invoker<DemoService> refer = protocol.refer(
                DemoService.class,
                URL.valueOf("native://127.0.0.1:8080/DemoService")
                        .addParameter(INTERFACE_KEY, DemoService.class.getName()));
        service = proxy.getProxy(refer);
        service.invoke("native://127.0.0.1:8080/DemoService", "invoke");

        NativeInvoker<?> nativeInvoker = new NativeInvoker<>(
                DemoService.class, URL.valueOf("native://127.0.0.1/TestService"), null, new HashMap<>());

    }
}