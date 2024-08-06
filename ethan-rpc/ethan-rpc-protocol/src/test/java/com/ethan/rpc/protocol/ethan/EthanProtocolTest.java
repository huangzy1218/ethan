package com.ethan.rpc.protocol.ethan;

import com.ethan.common.URL;
import com.ethan.model.ApplicationModel;
import com.ethan.rpc.*;
import com.ethan.rpc.protocol.injvm.InjvmInvoker;
import com.example.DemoService;
import com.example.DemoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.ethan.common.constant.CommonConstants.DEFAULT_PROXY;
import static com.ethan.common.constant.CommonConstants.REMOTE_PROTOCOL;

public class EthanProtocolTest {

    private final Protocol protocol = ApplicationModel.defaultModel()
            .getExtensionLoader(Protocol.class).getExtension(REMOTE_PROTOCOL);
    private final ProxyFactory proxy = ApplicationModel.defaultModel()
            .getExtensionLoader(ProxyFactory.class).getExtension(DEFAULT_PROXY);
    Class<?>[] types = new Class[]{String.class};
    Object[] args = new Object[]{(String) "Huang Z.Y."};
    Invocation invocation = new RpcInvocation("sayHello", DemoService.class.getName(),
            "default/DemoService:1.0", types, args);
    private Map<String, Exporter<?>> exporters = new HashMap<>();

    @Test
    void tesRemoteProtocol() throws Exception {
        DemoService service = new DemoServiceImpl();
        Invoker<DemoService> invoker = proxy.getInvoker(
                service, DemoService.class);
        Exporter<DemoService> exporter = protocol.export(invoker);
        Invoker<DemoService> refer = protocol.refer(DemoService.class, URL.buildFixedURL(), exporter);

        service = proxy.getProxy(refer);
        Object res1 = service.invoke("native://127.0.0.1:8080/DemoService", "invoke");
        Assertions.assertEquals("native://127.0.0.1:8080/DemoService:invoke", res1);

        URL serviceUrl = URL.valueOf("native://127.0.0.1/TestService");
        exporters.put(serviceUrl.getServiceKey(), exporter);
        InjvmInvoker<DemoService> injvmInvoker = new InjvmInvoker<>(
                DemoService.class, serviceUrl, exporter);
        Result res2 = injvmInvoker.invoke(invocation);
        Assertions.assertEquals("Huang Z.Y.", res2.getValue());
    }

}