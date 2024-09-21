package com.ethan.config.annotation;

import com.ethan.config.bootstrap.Bootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Huang Z.Y.
 */
@SpringBootApplication
public class HelloApplication {

    public static void main(String[] args) {
        Bootstrap ethan = Bootstrap.getInstance().application("ethan");
        ethan.initialize();
        SpringApplication.run(HelloApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(HelloApplication.class, args);

        HelloService helloService = context.getBean(HelloService.class);
        helloService.sayHello("Hello");
    }

}
