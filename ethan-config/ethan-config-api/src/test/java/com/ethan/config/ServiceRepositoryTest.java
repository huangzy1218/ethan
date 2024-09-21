package com.ethan.config;

import com.ethan.common.RemotingException;
import org.junit.Test;

public class ServiceRepositoryTest {

    @Test
    public void testRegisterService() throws RemotingException {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.setRef(new HelloServiceImpl());
        serviceConfig.setVersion("2.0.1");
        serviceConfig.setGroup("test");
        serviceConfig.setAsync(true);
        ServiceRepository serviceRepository = new ServiceRepository();
        serviceRepository.registerService(serviceConfig);
    }

}