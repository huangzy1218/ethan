package com.ethan.registry.client.redis;

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
     * @return {@code true} if key was deleted successfully, {@code false} otherwise
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

    /**
     * Sets the field in a hash stored at key to value.
     *
     * @param key   Redis key
     * @param field Field within the Redis hash
     * @param value Value to be set
     * @return {@code true} if the field was set, {@code false} if the key does not exist
     */
    public boolean hset(String key, String field, String value) {
        return client.hset(key, field, value) == 1;
    }

    /**
     * Retrieves the value associated with the field from a hash stored at key.
     *
     * @param key   Redis key
     * @param field Field within the Redis hash
     * @return Value associated with the field, or null if either the key or field does not exist
     */
    public String hget(String key, String field) {
        return client.hget(key, field);
    }

    /**
     * Deletes one or more fields from a hash stored at key.
     *
     * @param key    Redis key
     * @param fields Fields to delete
     * @return Number of fields that were removed from the hash, not including specified but non-existing fields
     */
    public Long hdel(String key, String... fields) {
        return client.hdel(key, fields);
    }

    /**
     * Checks if a field exists in a hash stored at key.
     *
     * @param key   Redis key
     * @param field Field within the Redis hash
     * @return {@code true} if the field exists, {@code false} otherwise
     */
    public boolean hexists(String key, String field) {
        return client.hexists(key, field);
    }

    /**
     * Disconnects from the Redis server.
     */
    public void disconnect() {
        client.quit();
        client.close();
    }

}
