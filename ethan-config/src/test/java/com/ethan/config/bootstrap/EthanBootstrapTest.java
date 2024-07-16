package com.ethan.config.bootstrap;


import org.junit.jupiter.api.Test;

public class EthanBootstrapTest {

    @Test
    void testBootstrap() {
        EthanBootstrap ethan = EthanBootstrap.getInstance().application("ethan");
    }

}