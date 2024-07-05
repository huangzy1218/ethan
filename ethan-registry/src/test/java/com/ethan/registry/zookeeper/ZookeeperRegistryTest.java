package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZookeeperRegistryTest {

    private URL registerUrl = URL.valueOf("zookeeper://39.107.235.5/ethan/default");
    private URL url = URL.valueOf("ethan://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group");
    private ZookeeperRegistry registry;

    @BeforeEach
    public void init() {
        registry = new ZookeeperRegistry(registerUrl);
    }

    @Test
    void register() {
        registry.register(url);
    }

    @Test
    void unregister() {
        registry.unregister(url);
    }

    @Test
    void lookup() {
        System.out.println(registry.lookup(url));
    }

    @Test
    void toUrlPath() {
        System.out.println(registry.toUrlPath(url));
    }

}