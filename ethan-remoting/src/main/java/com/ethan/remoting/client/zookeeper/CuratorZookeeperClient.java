package com.ethan.remoting.client.zookeeper;

import com.ethan.rpc.RpcConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@link CuratorZookeeperClient} is a utility class for managing a Zookeeper client using the Curator framework.
 *
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class CuratorZookeeperClient {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework client;

    private final RpcConfigProperties rpcConfigProperties;

    @Autowired
    public CuratorZookeeperClient(RpcConfigProperties rpcConfigProperties) {
        this.rpcConfigProperties = rpcConfigProperties;
    }

    /**
     * Get the Zookeeper client.
     *
     * @return Zookeeper client
     */
    public static CuratorFramework getClient() {
        // If Zookeeper has been started, return directly
        if (client != null && client.getState() == CuratorFrameworkState.STARTED) {
            return client;
        }
        initClient();
        return client;
    }

    private static void initClient() {
        // Check if the user has set a Zookeeper address
        // Properties properties = PropertiesFileUtils.readPropertiesFile(RPC_CONFIG_PATH);
        String address = rpcConfigProperties.getAddress();
        int sleepTime = rpcConfigProperties.getSleepTime();
        // Retry 3 times, which will increase the sleep time between retries
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sleepTime, MAX_RETRIES);
        client = CuratorFrameworkFactory.builder()
                // Server address to connect to
                .connectString(address)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        try {
            // Wait 30 seconds until Zookeeper is connected
            if (!client.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to Zookeeper!");
            }
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Create a persistent node. Unlike temporary nodes, persistent nodes are not deleted when the client disconnects.
     *
     * @param path Node path
     * @param data Real date
     */
    public static void createPersistent(String path, String data) {
        byte[] dataBytes = data.getBytes(CHARSET);
        try {
            if (!checkExists(path)) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                // eg: /ethan-rpc/cn.edu.nwafu.HelloService/127.0.0.1:9999
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, dataBytes);
                log.info("The persistence node was created successfully. The node is:[{}]", path);
            }
        } catch (Exception e) {
            log.error("Create persistent node for path [{}] fail", path);
        }
    }

    /**
     * Create a ephemeral node. Unlike temporary nodes, persistent nodes are not deleted when the client disconnects.
     *
     * @param path Node path
     * @param data Real date
     */
    public static void createEphemeral(String path, String data) {
        byte[] dataBytes = data.getBytes(CHARSET);
        try {
            if (!checkExists(path)) {
                log.info("The ephemeral node already exists. The node is:[{}]", path);
            } else {
                // eg: /ethan-rpc/cn.edu.nwafu.HelloService/127.0.0.1:9999
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, dataBytes);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
        } catch (Exception e) {
            log.error("Create ephemeral node for path [{}] fail", path);
        }
    }

    /**
     * Update the node data.
     *
     * @param path Node path
     * @param data Real date
     */
    public static void update(String path, String data) {
        byte[] dataBytes = data.getBytes(CHARSET);
        try {
            client.setData().forPath(path, dataBytes);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void createOrUpdatePersistent(String path, String data) {
        try {
            if (checkExists(path)) {
                update(path, data);
            } else {
                createPersistent(path, data);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    protected static void createOrUpdateEphemeral(String path, String data) {
        try {
            if (checkExists(path)) {
                update(path, data);
            } else {
                createEphemeral(path, data);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void deletePath(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (KeeperException.NoNodeException ignored) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static boolean checkExists(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }

}
