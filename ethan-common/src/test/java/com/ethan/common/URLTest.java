package com.ethan.common;


import org.junit.jupiter.api.Test;

class URLTest {

    @Test
    public void generateURLTest() {
        URL url = new URL("http", "localhost", 8080, "/greeting");
        url.addParam("param1", "value1");
        url.addParam("param2", "value2");
        System.out.println(url);
    }

}