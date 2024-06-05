package com.ethan.rpc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ethan.rpc.zookeeper")
@Data
public class RpcConfigProperties {

    private int sleepTime = 1000;
    private int maxRetries = 3;
    private String registerRootPath = "ethan";
    private String configPath = "rpc.properties";
    private String address = "127.0.0.1:2181";

    private Environment environment;

}
