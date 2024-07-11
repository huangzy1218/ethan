package com.ethan.remoting.client.redis;

import com.ethan.common.URL;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis transporter.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class RedisTransporter {

    private final static Map<String, RedisClient> redisClientMap = new ConcurrentHashMap<>();

    public static RedisClient connect(URL url) {
        RedisClient redisClient;
        // Address format: localhost:8080
        String address = url.getAddress();
        // The field define the redis server , including protocol, host, port, username, password
        if ((redisClient = fetchAndUpdateRedisClientCache(address)) != null
                && redisClient.isConnected()) {
            log.info("Find valid redis client from the cache for address: {}", url);
            return redisClient;
        }
        // Avoid creating too many connections， so add lock
        synchronized (redisClientMap) {
            if ((redisClient = fetchAndUpdateRedisClientCache(address)) != null) {
                log.info("Find valid redis client from the cache for address: {}", url);
                return redisClient;
            }

            redisClient = createRedisClient(url);
            log.info("No valid redis client found from cache, therefore create a new client for url. {}", url);
            redisClientMap.put(address, redisClient);
        }
        return redisClient;
    }

    public static RedisClient fetchAndUpdateRedisClientCache(String address) {
        return redisClientMap.get(address);
    }

    public static RedisClient createRedisClient(URL url) {
        return new RedisClient(url);
    }

}
