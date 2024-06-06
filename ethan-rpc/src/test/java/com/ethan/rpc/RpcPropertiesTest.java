package com.ethan.rpc;

import com.ethan.common.context.ApplicationContextProvider;
import com.ethan.rpc.config.RpcConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RpcConfiguration.class)
class RpcPropertiesTest {

    @Test
    public void testRpcProperties() {
        RpcProperties bean = ApplicationContextProvider.getBean(RpcProperties.class);
        System.out.println(bean);
    }

}