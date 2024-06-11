package com.ethan.remoting.client.zookeeper;

import com.ethan.common.URL;
import com.ethan.common.util.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.ethan.common.constant.CommonConstants.SESSION_KEY;
import static com.ethan.common.constant.CommonConstants.TIMEOUT_KEY;

/**
 * {@link ZookeeperClient} is a utility class for managing a Zookeeper client using the Curator framework.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ZookeeperClient {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework client;
    protected int DEFAULT_CONNECTION_TIMEOUT_MS = 30 * 1000;
    protected int DEFAULT_SESSION_TIMEOUT_MS = 60 * 1000;

    private volatile boolean closed = false;
    private final Set<String> persistentExistNodePath = new ConcurrentHashSet<>();


    private final URL url;

    public ZookeeperClient(URL url) {
        this.url = url;
        try {
            int timeout = url.getParameter(TIMEOUT_KEY, DEFAULT_CONNECTION_TIMEOUT_MS);
            int sessionExpireMs = url.getParameter(SESSION_KEY, DEFAULT_SESSION_TIMEOUT_MS);
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .retryPolicy(new RetryNTimes(1, 1000))
                    .connectionTimeoutMs(timeout)
                    .sessionTimeoutMs(sessionExpireMs);
            client = builder.build();
            client.start();

            boolean connected = client.blockUntilConnected(timeout, TimeUnit.MILLISECONDS);
            if (!connected) {
                IllegalStateException illegalStateException =
                        new IllegalStateException("zookeeper not connected, the address is: " + url);

                // 5-1 Failed to connect to configuration center.
                log.error("Zookeeper server offline,Failed to connect with zookeeper.", illegalStateException);

                throw illegalStateException;
            }

        } catch (Exception e) {
            close();
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;
    }

    /**
     * Get the Zookeeper client.
     *
     * @return Zookeeper client
     */
    public static CuratorFramework getClient() {
        // If Zookeeper has been started, return directly
        if (client != null) {
            client.getState();
        }
        return client;
    }

    public void create(String path, boolean ephemeral) {
        if (!ephemeral) {
            if (persistentExistNodePath.contains(path)) {
                return;
            }
            if (checkExists(path)) {
                persistentExistNodePath.add(path);
                return;
            }
        }
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
            persistentExistNodePath.add(path);
        }
    }

    public void createPersistent(String path) {
        try {
            client.create().forPath(path);
        } catch (Exception e) {
            log.warn("ZNode " + path + " already exists.", e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void createEphemeral(String path) {
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            log.warn("ZNode " + path + " already exists.", e);
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
