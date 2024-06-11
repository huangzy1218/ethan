package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.registry.NotifyListener;
import com.ethan.registry.Registry;
import com.ethan.remoting.client.zookeeper.ZookeeperClient;
import com.ethan.remoting.client.zookeeper.ZookeeperTransporter;
import com.ethan.rpc.RpcException;
import lombok.extern.slf4j.Slf4j;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * The implementation of {@link com.ethan.registry.Registry} using Zookeepr.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ZookeeperRegistry implements Registry {

    private ZookeeperClient zkClient;

    private static final String DEFAULT_ROOT = "ethan";

    private final String root;
    private URL registryUrl;


    public ZookeeperRegistry(URL url) {
        this.registryUrl = url;
        String PATH_SEPARATOR = "/";
        String group = url.getGroup(DEFAULT_ROOT);
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
            zkClient.create(toUrlPath(url), url.getParameter(DYNAMIC_KEY, true), true);
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregister(URL url) {

    }

    @Override
    public void subscribe(URL url, NotifyListener listener) {

    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {

    }

    @Override
    public URL lookup(URL url) {
        return null;
    }

    private String toUrlPath(URL url) {
        return toCategoryPath(url) + PATH_SEPARATOR + URL.encode(url.toFullString());
    }

    private String toCategoryPath(URL url) {
        return toServicePath(url) + PATH_SEPARATOR + url.getCategory(DEFAULT_CATEGORY);
    }

    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        if (ANY_VALUE.equals(name)) {
            return toRootPath();
        }
        return toRootDir() + URL.encode(name);
    }

}
