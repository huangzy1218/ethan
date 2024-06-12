package com.ethan.registry.zookeeper;

import com.ethan.common.URL;
import com.ethan.common.util.StringUtils;
import org.junit.jupiter.api.Test;

import static com.ethan.common.constant.CommonConstants.GROUP_KEY;
import static com.ethan.common.constant.CommonConstants.VERSION_KEY;

class ZookeeperRegistryTest {

    URL url = URL.valueOf("dubbo://127.0.0.1:20880/com.example.DemoService?version=1.0.0&group=test-group");

    @Test
    void getUrlTest() {
        System.out.println(toUrlPath(url));
    }


    String toUrlPath(URL url) {
        String interfaceName = url.getServiceInterface();
        String version = url.getParameter(VERSION_KEY);
        String group = url.getParameter(GROUP_KEY);
        StringBuilder path = new StringBuilder();
        path.append("ethan").append("/");
        if (StringUtils.isBlank(group)) {
            path.append(group).append("/");
        }
        path.append(interfaceName);
        if (version != null && !version.isEmpty()) {
            path.append(":").append(version);
        }
        return path.toString();
    }
    
}