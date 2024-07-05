package com.ethan.registry.redis;

import com.ethan.common.URL;
import com.ethan.registry.support.AbstractRegistry;
import com.ethan.remoting.client.redis.RedisClient;
import com.ethan.remoting.client.redis.RedisTransporter;
import com.ethan.rpc.RpcException;
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
        return null;
    }

    @Override
    public void register(URL url) {
        try {
            checkDestroyed();
            redisClient.set(toUrlPath(url), url.getParameter(DYNAMIC_KEY, true));
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregister(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("unregister url == null");
        }
        if (url.getPort() != 0) {
            log.info("Unregister: {}", url);
        }
        registered.remove(url);
    }

    private void checkDestroyed() {
        if (redisClient == null) {
            throw new IllegalStateException("Registry is destroyed");
        }
    }

}
