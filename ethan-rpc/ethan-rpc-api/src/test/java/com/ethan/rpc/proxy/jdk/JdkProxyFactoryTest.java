package com.ethan.rpc.proxy.jdk;


import com.ethan.common.URL;
import com.ethan.rpc.Invoker;
import com.example.HelloService;
import com.example.HelloServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JdkProxyFactoryTest {

    @Test
    public void proxy() throws Exception {
        // Create JdkProxyFactory instance
        JdkProxyFactory proxyFactory = new JdkProxyFactory();
        // Create UserService instance
        HelloService userService = new HelloServiceImpl();

        // Get invoker
        URL url = URL.valueOf("http://localhost:8080/helloService");
        Invoker<HelloService> invoker = proxyFactory.getInvoker(userService, HelloService.class, url);
        // Get proxy
        HelloService proxy = proxyFactory.getProxy(invoker, new Class[]{HelloService.class});
        // Call the proxy method
        String result = proxy.sayHello("World");
        Assertions.assertEquals("Hello, World", result);
    }

}