package com.ethan.remoting.client.redis;

import com.ethan.common.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RedisClientTest {

    private URL url = URL.valueOf("redis://39.107.235.5:6379");
    private RedisClient client = RedisTransporter.connect(url);

    @Test
    public void set() {
        client.set("com.example.A", "Hello World");
    }

    @Test
    void get() {
        String value = client.get("com.example.A");
        Assertions.assertEquals("Hello World", value);
    }

    @Test
    void delete() {
        client.delete("com.example.A");
        Assertions.assertFalse(client.exists("com.example.A"));
    }
    
}