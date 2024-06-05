package com.ethan.rpc;

import com.ethan.common.context.ApplicationContextProvider;
import com.ethan.rpc.config.RpcConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RpcConfiguration.class)
class RpcConfigPropertiesTest {

    private final RpcConfigProperties rpcConfigProperties;

    @Autowired
    public RpcConfigPropertiesTest(RpcConfigProperties rpcConfigProperties) {
        this.rpcConfigProperties = rpcConfigProperties;
    }

    @Test
    public void testRpcProperties() {
        RpcConfigProperties bean = ApplicationContextProvider.getBean(RpcConfigProperties.class);
        System.out.println(rpcConfigProperties);
    }

}