package com.ethan.remoting.client.zookeeper;

import com.ethan.common.URL;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zookeeper transporter.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ZookeeperTransporter {

    private final static Map<String, ZookeeperClient> zookeeperClientMap = new ConcurrentHashMap<>();

    public static ZookeeperClient connect(URL url) {
        ZookeeperClient zookeeperClient;
        // Address format: localhost:8080
        String address = url.getAddress();
        // The field define the zookeeper server , including protocol, host, port, username, password
        if ((zookeeperClient = fetchAndUpdateZookeeperClientCache(address)) != null
                && ZookeeperClient.isConnected()) {
            log.info("Find valid zookeeper client from the cache for address: {}", url);
            return zookeeperClient;
        }
        // Avoid creating too many connections， so add lock
        synchronized (zookeeperClientMap) {
            if ((zookeeperClient = fetchAndUpdateZookeeperClientCache(address)) != null) {
                log.info("Find valid zookeeper client from the cache for address: {}", url);
                return zookeeperClient;
            }

            zookeeperClient = createZookeeperClient(url);
            log.info("No valid zookeeper client found from cache, therefore create a new client for url. {}", url);
            zookeeperClientMap.put(address, zookeeperClient);
        }
        return zookeeperClient;
    }

    public static ZookeeperClient fetchAndUpdateZookeeperClientCache(String address) {
        return zookeeperClientMap.get(address);
    }

    public static ZookeeperClient createZookeeperClient(URL url) {
        return new ZookeeperClient(url);
    }

}
