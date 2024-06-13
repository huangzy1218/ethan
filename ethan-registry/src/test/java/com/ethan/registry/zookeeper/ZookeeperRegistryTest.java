package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.rpc.RpcException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.ethan.common.constant.CommonConstants.*;

class ZookeeperRegistryTest {

    URL url = URL.valueOf("ethan://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group");

    private String root = "/ethan";

    @Test
    void getUrlTest() {
        System.out.println(url.getServiceInterface());
        System.out.println(toUrlPath(url));
    }

    @Test
    void toCategoriesPathTest() {
        String[] categoriesPath = toCategoriesPath(url);
        System.out.println(categoriesPath);
    }

    @Test
    void lookupTest() {
        List<URL> urls = lookup(url);
        System.out.println(urls);
    }

    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        try {
            List<String> providers = new ArrayList<>();
            for (String path : toCategoriesPath(url)) {
                System.out.println(toCategoriesPath(url));
                providers.add(path);
                System.out.println("Path: " + path);
            }
            return toUrlsWithoutEmpty(providers);
        } catch (Throwable e) {
            throw new RpcException("Failed to lookup " + url + " from zookeeper " + url + ", cause: " + e.getMessage(), e);
        }
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

    private String[] toCategoriesPath(URL url) {
        String[] categories;
        if (ANY_VALUE.equals(url.getCategory())) {
            categories = new String[]{PROVIDERS_CATEGORY, CONSUMERS_CATEGORY};
        } else {
            categories = url.getCategory(new String[]{DEFAULT_CATEGORY});
        }
        String[] paths = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            paths[i] = toServicePath(url) + PATH_SEPARATOR + categories[i];
        }
        return paths;
    }

    private List<URL> toUrlsWithoutEmpty(List<String> providers) {
        List<URL> urls = new ArrayList<>();
        if (providers != null && !providers.isEmpty()) {
            for (String provider : providers) {
                URL url = URL.valueOf(provider);
                urls.add(url);
            }
        }
        return urls;
    }

}