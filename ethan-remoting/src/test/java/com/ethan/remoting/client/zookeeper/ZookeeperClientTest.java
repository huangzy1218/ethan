package com.ethan.remoting.zookeeper;

import com.ethan.common.URL;
import com.ethan.remoting.client.zookeeper.ZookeeperClient;
import com.ethan.remoting.client.zookeeper.ZookeeperTransporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Huang Z.Y.
 */
class ZookeeperClientTest {

    private URL url = URL.valueOf("zookeeper://39.107.235.5:2181");
    private ZookeeperClient client = ZookeeperTransporter.connect(url);

    @Test
    void createPersistent() {
        // client.createPersistent("/ethan/node1/node11");
        client.createPersistent("/example/node2/node21", "node21");
    }

    @Test
    void createEphemeral() {
        // client.createEphemeral("/ethan/node1/node21");
        client.createEphemeral("/ethan/node21/node22", "node22");
    }

    @Test
    void update() {
        client.update("/example/node2/node21", "node 21");
    }

    @Test
    void deletePath() {
        client.delete("/example");
    }

    @Test
    void isConnected() {
        Assertions.assertTrue(ZookeeperClient.isConnected());
    }

    @Test
    void delete() {
        client.delete("/ethan/node21/node22");
    }

    @Test
    void getChildren() {
        System.out.println(client.getChildren("/ethan"));
    }

}