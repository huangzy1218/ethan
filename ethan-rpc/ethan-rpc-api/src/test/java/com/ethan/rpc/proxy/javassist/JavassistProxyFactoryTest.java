package com.ethan.rpc.proxy.javassist;

import com.ethan.model.ApplicationModel;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
import com.example.HelloService;
import com.example.HelloServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.ethan.common.constant.CommonConstants.DEFAULT_PROXY;

public class JavassistProxyFactoryTest {

    @Test
    void proxy() throws Exception {
        // Create JavassistProxyFactory instance
        ProxyFactory proxyFactory = ApplicationModel.defaultModel()
                .getExtensionLoader(ProxyFactory.class).getExtension(DEFAULT_PROXY);
//        JavassistProxyFactory proxyFactory = new JavassistProxyFactory();
        // Create UserService instance
        HelloService userService = new HelloServiceImpl();

        Invoker<HelloService> invoker = proxyFactory.getInvoker(userService, HelloService.class);
        // Get proxy
        HelloService proxy = proxyFactory.getProxy(invoker);
        // Call the proxy method
        String result = proxy.sayHello("World");
        Assertions.assertEquals("Hello, World", result);
    }

}