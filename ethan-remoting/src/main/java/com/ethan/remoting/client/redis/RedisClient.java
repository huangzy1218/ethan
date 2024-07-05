package com.ethan.remoting.client.redis;

import com.ethan.common.URL;
import redis.clients.jedis.Jedis;

/**
 * {@link RedisClient} is a utility class that provides methods to interact with Redis,
 * serving as a client for various Redis operations.
 *
 * @author Huang Z.Y.
 */
public class RedisClient {

    private Jedis client;

    public RedisClient(URL url) {
        String host = url.getHost();
        int port = url.getPort();
        client = new Jedis(host, port);
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * Sets the value of a key in Redis.
     *
     * @param key   Redis key
     * @param value Redis value
     */
    public void set(String key, String value) {
        client.set(key, value);
    }

    /**
     * Retrieves the value associated with a key from Redis.
     *
     * @param key Redis key
     * @return Value associated with the key, or null if key does not exist
     */
    public String get(String key) {
        return client.get(key);
    }

    /**
     * Deletes a key from Redis.
     *
     * @param key Redis key
     * @return true if key was deleted successfully, false otherwise
     */
    public Long delete(String key) {
        return client.del(key);
    }

    /**
     * Checks if a key exists in Redis.
     *
     * @param key Redis key
     * @return {@code true} if key exists, false otherwise
     */
    public boolean exists(String key) {
        return client.exists(key);
    }

}
