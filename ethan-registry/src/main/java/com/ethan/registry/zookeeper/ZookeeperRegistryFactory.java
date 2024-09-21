package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.registry.Registry;
import com.ethan.registry.RegistryFactory;

/**
 * @author Huang Z.Y.
 */
public class ZookeeperRegistryFactory implements RegistryFactory {
    
    @Override
    public Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url);
    }

}
