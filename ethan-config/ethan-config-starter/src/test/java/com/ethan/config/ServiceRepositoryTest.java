package com.ethan.config;

import com.ethan.common.RemotingException;
import com.ethan.config.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ServiceRepositoryTest {


    @Test
    public void testRegisterService() throws RemotingException {
        Bootstrap.getInstance().application("ethan").initialize();
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.setInterfaceName(HelloService.class.getName());
        serviceConfig.setRef(new HelloServiceImpl());
        serviceConfig.setVersion("2.0.1");
        serviceConfig.setGroup("test");
        serviceConfig.setAsync(true);
        ServiceRepository serviceRepository = new ServiceRepository();
        serviceRepository.registerService(serviceConfig);
    }

}