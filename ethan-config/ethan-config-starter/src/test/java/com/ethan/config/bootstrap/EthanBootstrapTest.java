package com.ethan.config.bootstrap;


import org.junit.jupiter.api.Test;

public class EthanBootstrapTest {

    @Test
    void testBootstrap() {
        Bootstrap ethan = Bootstrap.getInstance().application("ethan");
        ethan.initialize();
        System.out.println(ethan.getConfigManager().getRegistry());
    }

}