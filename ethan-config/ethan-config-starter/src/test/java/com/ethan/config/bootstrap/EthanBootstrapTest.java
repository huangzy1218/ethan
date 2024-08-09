package com.ethan.config.bootstrap;


import org.junit.jupiter.api.Test;

public class EthanBootstrapTest {

    @Test
    void testBootstrap() {
        SysProps.setProperty("ethan.registry.address", "127.0.0.1");
        SysProps.setProperty("ethan.registry.port", 8080);
        Bootstrap ethan = Bootstrap.getInstance().application("ethan");
        ethan.initialize();
        System.out.println(ethan.getConfigManager().getRegistry());
    }

}