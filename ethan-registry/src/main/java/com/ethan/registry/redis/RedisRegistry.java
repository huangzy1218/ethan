package com.ethan.registry.redis;

import com.ethan.common.RpcException;
import com.ethan.common.URL;
import com.ethan.registry.client.redis.RedisClient;
import com.ethan.registry.client.redis.RedisTransporter;
import com.ethan.registry.support.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * he implementation of {@link com.ethan.registry.Registry} using Redis.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class RedisRegistry extends AbstractRegistry {

    private final RedisClient redisClient;
    private final String root;
    private URL registryUrl;

    public RedisRegistry(URL url) {
        this.registryUrl = url;
        String group = url.getGroup(ETHAN);
        if (!group.startsWith(PATH_SEPARATOR)) {
            group = PATH_SEPARATOR + group;
        }
        this.root = group;
        redisClient = RedisTransporter.createRedisClient(registryUrl);

    }

    @Override
    public List<URL> lookup(URL url) {
        return List.of();
    }

    @Override
    public URL getUrl() {
        return registryUrl;
    }

    @Override
    public void register(URL url) {
        try {
            checkDestroyed();
            String key = toCategoryPath(url);
            String providerAddress = url.getAddress();
            redisClient.set(key, providerAddress);
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to redis " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregister(URL url) {
        try {
            checkDestroyed();
            redisClient.delete(toCategoryPath(url));
        } catch (Throwable e) {
            throw new RpcException("Failed to unregister " + url + " to redis " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    private void checkDestroyed() {
        if (redisClient == null) {
            throw new IllegalStateException("Registry is destroyed");
        }
    }

    public String toUrlPath(URL url) {
        return toCategoryPath(url) + url.toFullString();
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

}
