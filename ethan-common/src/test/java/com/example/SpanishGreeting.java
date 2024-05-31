package com.example;

import com.ethan.common.extension.SPI;

@SPI("spanish")
public class SpanishGreeting implements Greeting {

    @Override
    public String greet() {
        return "Hola";
    }

}
