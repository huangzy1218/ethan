package com.example;

/**
 * @author Huang Z.Y.
 */
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return name;
    }

    @Override
    public int plus(int a, int b) {
        return 0;
    }


    public Object invoke(String service, String method) throws Exception {
        System.out.println(service + ":" + method);
        return service + ":" + method;
    }
}

    