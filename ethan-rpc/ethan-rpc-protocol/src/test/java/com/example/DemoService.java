package com.example;

/**
 * @author Huang Z.Y.
 */
public interface DemoService {

    String sayHello(String name);

    int plus(int a, int b);

    Object invoke(String service, String method) throws Exception;

}
    