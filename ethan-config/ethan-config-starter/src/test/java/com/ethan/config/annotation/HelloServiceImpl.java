package com.ethan.config.annotation;

import org.springframework.stereotype.Component;

/**
 * @author Huang Z.Y.
 */
@Component
@Service(group = "default", version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }

}
