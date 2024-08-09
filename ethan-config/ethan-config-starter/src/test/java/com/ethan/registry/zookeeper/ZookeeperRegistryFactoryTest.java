package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.model.ApplicationModel;
import org.junit.Test;

/**
 * @author Huang Z.Y.
 */
public class ZookeeperRegistryFactoryTest {

    @Test
    public void testZookeeperRegistryFactory() {
        ZookeeperRegistryFactory factory = ApplicationModel.defaultModel()
                .getExtensionLoader(ZookeeperRegistryFactory.class).getExtension("zookeeper");
        URL url = URL.valueOf("zookeeper://39.107.235.5");
        factory.createRegistry(url);
    }

}
