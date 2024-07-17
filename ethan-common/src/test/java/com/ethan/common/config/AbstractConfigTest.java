package com.ethan.common.config;

import com.ethan.common.bean.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractConfigTest {

    @Test
    void testInvokeSetter() {
        TestBean bean = new TestBean();
        AbstractConfig.invokeSetter(TestBean.class, bean, "str", "Hello");
        AbstractConfig.invokeSetter(TestBean.class, bean, "i", 1);
        Assertions.assertEquals("Hello", bean.getStr());
        Assertions.assertEquals(1, bean.getI());
    }

}