package com.ethan.common.context;


import com.ethan.common.url.component.URLParam;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
class BeanProviderTest {

    @Test
    void testGetBeanFromContext() {
        URLParam bean = ApplicationContextHolder.getBean(URLParam.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        bean.setParameters(map);
        System.out.println(bean);
    }

}