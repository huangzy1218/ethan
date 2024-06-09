package com.ethan.rpc;

import com.ethan.common.context.BeanProvider;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class RpcPropertiesTest {

    @Test
    public void testRpcProperties() {
        RpcProperties bean = BeanProvider.getBean(RpcProperties.class);
        System.out.println(bean);
    }

}