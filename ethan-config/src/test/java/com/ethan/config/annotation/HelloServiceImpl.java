package com.ethan.config.annotation;

/**
 * @author Huang Z.Y.
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }

}
