package com.ethan.rpc;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "ethan.rpc.zookeeper")
@Data
public class RpcProperties implements InitializingBean {

    private int sleepTime = 1000;
    private int maxRetries = 3;
    private String registerRootPath = "ethan";
    private String configPath = "rpc.properties";
    private String address = "127.0.0.1:2181";


    @Override
    public void afterPropertiesSet() throws Exception {
        loadProperties();
    }

    @SuppressWarnings("unchecked")
    private void loadProperties() {
        Yaml yaml = new Yaml();
        Resource resource = new ClassPathResource("application.yml");
        try {
            Map<String, Object> yamlMap = yaml.load(resource.getInputStream());
            if (yamlMap != null) {
                Map<String, Object> ethanMap = (Map<String, Object>) yamlMap.get("ethan");
                if (ethanMap != null) {
                    Map<String, Object> rpcMap = (Map<String, Object>) ethanMap.get("rpc");
                    if (rpcMap != null) {
                        Map<String, Object> zookeeperMap = (Map<String, Object>) rpcMap.get("zookeeper");
                        if (zookeeperMap != null) {
                            Object sleepTimeObj = zookeeperMap.get("sleep-time");
                            if (sleepTimeObj != null) {
                                this.sleepTime = Integer.parseInt(sleepTimeObj.toString());
                            }
                            Object maxRetriesObj = zookeeperMap.get("max-retries");
                            if (maxRetriesObj != null) {
                                this.maxRetries = Integer.parseInt(maxRetriesObj.toString());
                            }
                            Object registerRootPathObj = zookeeperMap.get("register-root-path");
                            if (registerRootPathObj != null) {
                                this.registerRootPath = registerRootPathObj.toString();
                            }

                            Object configPathObj = zookeeperMap.get("config-path");
                            if (configPathObj != null) {
                                this.configPath = configPathObj.toString();
                            }

                            Object addressObj = zookeeperMap.get("address");
                            if (addressObj != null) {
                                this.address = addressObj.toString();
                            }
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
}
