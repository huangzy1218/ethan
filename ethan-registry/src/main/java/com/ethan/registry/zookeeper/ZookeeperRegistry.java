package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.common.util.StringUtils;
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
    /**
     * ethan://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group"
     */
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
            zkClient.create(toUrlPath(url), url.getParameter(DYNAMIC_KEY, true));
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


    public URL getUrl() {
        return registryUrl;
    }

    private String toUrlPath(URL url) {
        String interfaceName = url.getServiceInterface();
        String version = url.getParameter(VERSION_KEY);
        String group = url.getParameter(GROUP_KEY);
        StringBuilder path = new StringBuilder();
        path.append(root).append("/");
        if (StringUtils.isBlank(group)) {
            path.append(group).append("/");
        }
        path.append(interfaceName);
        if (version != null && !version.isEmpty()) {
            path.append(":").append(version);
        }
        return path.toString();
    }

}
