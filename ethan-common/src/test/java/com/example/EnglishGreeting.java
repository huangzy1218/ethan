package com.example;

import com.ethan.common.extension.SPI;

@SPI("english")
public class EnglishGreeting implements Greeting {

    @Override
    public String greet() {
        return "Hello";
    }

}

