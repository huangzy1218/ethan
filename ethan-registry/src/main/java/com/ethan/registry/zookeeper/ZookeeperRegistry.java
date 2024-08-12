package com.ethan.registry.zookeeper;

import com.ethan.common.RpcException;
import com.ethan.common.URL;
import com.ethan.registry.client.zookeeper.ZookeeperClient;
import com.ethan.registry.client.zookeeper.ZookeeperTransporter;
import com.ethan.registry.support.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * The implementation of {@link com.ethan.registry.Registry} using Zookeeper.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    private final ZookeeperClient zkClient;
    private final String root;
    /**
     * ethan://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group
     */
    private URL registryUrl;


    public ZookeeperRegistry(URL url) {
        this.registryUrl = url;
        String group = url.getGroup(ETHAN);
        if (!group.startsWith(PATH_SEPARATOR)) {
            group = PATH_SEPARATOR + group;
        }
        this.root = group;
        this.zkClient = ZookeeperTransporter.connect(url);

    }

    public boolean isAvailable() {
        return zkClient != null && zkClient.isConnected();
    }

    private void checkDestroyed() {
        if (zkClient == null) {
            throw new IllegalStateException("Registry is destroyed");
        }
    }

    @Override
    public void register(URL url) {
        try {
            checkDestroyed();
            zkClient.create(toUrlPath(url), url.getParameter(DYNAMIC_KEY, true));
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregister(URL url) {
        try {
            checkDestroyed();
            zkClient.delete(toUrlPath(url));
        } catch (Throwable e) {
            throw new RpcException("Failed to unregister " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        try {
            checkDestroyed();
            List<String> providers = new ArrayList<>();
            for (String path : toCategoriesPath(url)) {
                List<String> children = zkClient.getChildren(path);
                if (children != null) {
                    providers.addAll(children);
                }
            }
            return toUrlsWithoutEmpty(providers);
        } catch (Throwable e) {
            throw new RpcException("Failed to lookup " + url + " from zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }


    @Override
    public URL getUrl() {
        return registryUrl;
    }

    public String toUrlPath(URL url) {
        return toCategoryPath(url) + PATH_SEPARATOR + url.toFullString();
    }

    private String toCategoryPath(URL url) {
        return toServicePath(url) + PATH_SEPARATOR + url.getCategory(DEFAULT_CATEGORY);
    }

    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        if (ANY_VALUE.equals(name)) {
            return toRootPath();
        }
        return toRootDir() + PATH_SEPARATOR + name;
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
