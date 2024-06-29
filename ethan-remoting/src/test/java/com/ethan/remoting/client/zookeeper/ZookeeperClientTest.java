package com.ethan.remoting.client.zookeeper;

import com.ethan.common.URL;
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
        client.createPersistent("/ethan");
    }

    @Test
    void createEphemeral() {
    }

    @Test
    void update() {
    }

    @Test
    void deletePath() {
    }

    @Test
    void isConnected() {
        Assertions.assertTrue(ZookeeperClient.isConnected());
    }

    @Test
    void delete() {
    }
}