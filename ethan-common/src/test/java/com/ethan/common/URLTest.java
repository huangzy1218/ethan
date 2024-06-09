package com.ethan.common;


import org.junit.jupiter.api.Test;

class URLTest {

    @Test
    public void generateURLTest() {
        URL url = new URL("http", "localhost", 8080, "greeting");
        url.addParameter("param1", "value1");
        url.addParameter("param2", "value2");
        System.out.println(url);
        System.out.println(url.getServiceKey());
    }

    @Test
    void valueOfTest() {
        URL url = URL.valueOf("exchange://localhost:" + 8080 + "?server=netty4");
        System.out.println(url);
    }
}