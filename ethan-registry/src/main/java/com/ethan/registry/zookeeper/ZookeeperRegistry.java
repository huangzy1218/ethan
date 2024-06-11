package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.registry.NotifyListener;
import com.ethan.registry.Registry;
import com.ethan.remoting.client.zookeeper.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
        this.zkClient = zookeeperTransporter.connect(url);

    }

    @Override
    public void register(URL url) {

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


    public ZookeeperClient connect(URL url) {
        ZookeeperClient zookeeperClient;
        // Address format: {[username:password@]address}
        List<String> addressList = getURLBackupAddress(url);
        // The field define the zookeeper server , including protocol, host, port, username, password
        if ((zookeeperClient = fetchAndUpdateZookeeperClientCache(addressList)) != null
                && zookeeperClient.isConnected()) {
            log.info("find valid zookeeper client from the cache for address: " + url);
            return zookeeperClient;
        }
        // Avoid creating too many connections， so add lock
        synchronized (zookeeperClientMap) {
            if ((zookeeperClient = fetchAndUpdateZookeeperClientCache(addressList)) != null
                    && zookeeperClient.isConnected()) {
                logger.info("find valid zookeeper client from the cache for address: " + url);
                return zookeeperClient;
            }

            zookeeperClient = createZookeeperClient(url);
            logger.info("No valid zookeeper client found from cache, therefore create a new client for url. " + url);
            writeToClientMap(addressList, zookeeperClient);
        }
        return zookeeperClient;
    }
}
