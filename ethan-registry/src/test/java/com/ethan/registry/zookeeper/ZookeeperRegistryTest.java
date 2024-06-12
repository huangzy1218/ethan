package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import org.junit.jupiter.api.Test;

import static com.ethan.common.constant.CommonConstants.*;

class ZookeeperRegistryTest {

    URL url = URL.valueOf("ethan://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group");

    private String root = "ethan";

    @Test
    void getUrlTest() {
        System.out.println(url.getServiceInterface());
        System.out.println(toUrlPath(url));
    }


    private String toUrlPath(URL url) {
        return PATH_SEPARATOR + toCategoryPath(url) + url.toFullString();
    }

    private String toCategoryPath(URL url) {
        return toServicePath(url) + PATH_SEPARATOR + url.getCategory(DEFAULT_CATEGORY);
    }

    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        if (ANY_VALUE.equals(name)) {
            return toRootPath();
        }
        return toRootDir() + name;
    }

    private String toRootPath() {
        return root;
    }

    private String toRootDir() {
        return root;
    }

}