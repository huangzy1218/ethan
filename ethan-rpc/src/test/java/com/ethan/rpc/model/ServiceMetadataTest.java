package com.ethan.rpc.model;

import org.junit.jupiter.api.Test;

class ServiceMetadataTest {

    @Test
    public void serviceMetadataTest() {
        ServiceMetadata data = new ServiceMetadata("greeting", "test", "1.0");
        System.out.println(data);
    }
    
}