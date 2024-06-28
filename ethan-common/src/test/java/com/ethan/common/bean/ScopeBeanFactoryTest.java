package com.ethan.common.bean;


import org.junit.jupiter.api.Test;

class ScopeBeanFactoryTest {

    @Test
    public void registerBean() {
        ScopeBeanFactory scopeBeanFactory = new ScopeBeanFactory(null);
        scopeBeanFactory.registerBean(new TestBean());
        TestBean bean = scopeBeanFactory.getBean(TestBean.class);
        System.out.printf(bean.toString());
    }

}