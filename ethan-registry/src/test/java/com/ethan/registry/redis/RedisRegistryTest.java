package com.ethan.registry.redis;

import com.ethan.common.URL;
import org.junit.jupiter.api.Test;

class RedisRegistryTest {

    private URL registerUrl = URL.valueOf("redis://39.107.235.5:6379/ethan/default");
    private URL url = URL.valueOf("ethan://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group");
    private RedisRegistry registry = new RedisRegistry(registerUrl);

    @Test
    void register() {
        registry.register(url);
    }

    @Test
    void unregister() {
        registry.unregister(url);
    }
    
}