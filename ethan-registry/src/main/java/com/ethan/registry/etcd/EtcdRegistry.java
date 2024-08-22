package com.ethan.registry.etcd;

import com.ethan.common.RpcException;
import com.ethan.common.URL;
import com.ethan.registry.support.AbstractRegistry;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * The implementation of {@link com.ethan.registry.Registry} using Etcd.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class EtcdRegistry extends AbstractRegistry {

    private final Client client;
    private final KV kvClient;
    private final String root;
    private final Map<String, String> localRegisterNodeKeyMap = new ConcurrentHashMap<>();
    private URL registryUrl;

    public EtcdRegistry(URL url) {
        this.registryUrl = url;
        String group = url.getGroup(ETHAN);
        if (!group.startsWith(PATH_SEPARATOR)) {
            group = PATH_SEPARATOR + group;
        }
        this.root = group;

        // Initialize Etcd client
        this.client = Client.builder()
                .endpoints(url.getAddress())
                .connectTimeout(Duration.ofMillis(url.getTimeout()))
                .build();
        this.kvClient = client.getKVClient();
    }

    public boolean isAvailable() {
        return client != null && kvClient != null;
    }

    private void checkDestroyed() {
        if (client == null || kvClient == null) {
            throw new IllegalStateException("Registry is destroyed");
        }
    }

    @Override
    public void register(URL url) {
        try {
            checkDestroyed();
            Lease leaseClient = client.getLeaseClient();
            long leaseId = leaseClient.grant(30).get().getID();

            String registerKey = toUrlPath(url);
            ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
            ByteSequence value = ByteSequence.from(url.toFullString(), StandardCharsets.UTF_8);

            PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
            kvClient.put(key, value, putOption).get();
            localRegisterNodeKeyMap.put(registerKey, url.toFullString());
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to etcd " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregister(URL url) {
        try {
            checkDestroyed();
            String registerKey = toUrlPath(url);
            kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
            localRegisterNodeKeyMap.remove(registerKey);
        } catch (Throwable e) {
            throw new RpcException("Failed to unregister " + url + " from etcd " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        try {
            checkDestroyed();
            String searchPrefix = toUrlPath(url) + PATH_SEPARATOR;
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get().getKvs();

            return keyValues.stream()
                    .map(kv -> {
                        String value = kv.getValue().toString(StandardCharsets.UTF_8);
                        return URL.valueOf(value);
                    })
                    .collect(Collectors.toList());
        } catch (Throwable e) {
            throw new RpcException("Failed to lookup " + url + " from etcd " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public URL getUrl() {
        return registryUrl;
    }

    private String toUrlPath(URL url) {
        return toCategoryPath(url) + PATH_SEPARATOR + url.toFullString();
    }

    private String toCategoryPath(URL url) {
        return toServicePath(url) + PATH_SEPARATOR + url.getCategory(DEFAULT_CATEGORY);
    }

    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        if (ANY_VALUE.equals(name)) {
            return toRootPath();
        }
        return toRootDir() + PATH_SEPARATOR + name;
    }

    private String toRootPath() {
        return root;
    }

    private String toRootDir() {
        return root;
    }

    public void destroy() {
        log.info("Destroying registry");
        for (String key : localRegisterNodeKeyMap.keySet()) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                log.error("Failed to unregister key {}", key, e);
            }
        }
        client.close();
    }
}
