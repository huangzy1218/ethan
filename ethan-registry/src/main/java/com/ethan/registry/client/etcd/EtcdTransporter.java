package com.ethan.registry.client.etcd;

import io.etcd.jetcd.Client;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Etcd transporter.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class EtcdTransporter {

    private final static Map<String, Client> etcdClientMap = new ConcurrentHashMap<>();

    public static Client connect(String url) {
        Client etcdClient;
        //  Address format: localhost:2379
        if ((etcdClient = fetchAndUpdateEtcdClientCache(url)) != null && isConnected(etcdClient)) {
            log.info("Find valid etcd client from the cache for address: {}", url);
            return etcdClient;
        }
        synchronized (etcdClientMap) {
            if ((etcdClient = fetchAndUpdateEtcdClientCache(url)) != null) {
                log.info("Find valid etcd client from the cache for address: {}", url);
                return etcdClient;
            }
            etcdClient = createEtcdClient(url);
            log.info("No valid etcd client found from cache, therefore create a new client for url. {}", url);
            etcdClientMap.put(url, etcdClient);
        }
        return etcdClient;
    }

    public static Client fetchAndUpdateEtcdClientCache(String address) {
        return etcdClientMap.get(address);
    }

    public static Client createEtcdClient(String url) {
        return Client.builder().endpoints(url).build();
    }

    private static boolean isConnected(Client client) {
        // Default connected
        return true;
    }
}
