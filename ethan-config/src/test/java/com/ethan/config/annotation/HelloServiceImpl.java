package com.ethan.config.annotation;

import org.springframework.stereotype.Component;

/**
 * @author Huang Z.Y.
 */
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }

}
