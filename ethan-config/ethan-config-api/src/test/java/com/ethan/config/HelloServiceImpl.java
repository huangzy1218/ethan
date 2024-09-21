package com.ethan.config;

/**
 * @author Huang Z.Y.
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }

}
