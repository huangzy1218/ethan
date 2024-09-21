package com.ethan.config.annotation;

import org.springframework.stereotype.Component;

/**
 * @author Huang Z.Y.
 */
@Component
public class HelloServiceDemo {

    @Reference(group = "test", version = "1.0")
    private HelloService helloService;

    public void helloFromHelloService() {
        helloService.sayHello("Huang Z.Y.");
    }

}
