package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.util.ReflectUtils;
import com.ethan.common.util.StringUtils;
import com.ethan.config.annotation.Reference;
import com.ethan.registry.Registry;
import com.ethan.remoting.Transporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.model.ApplicationModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class ServiceBeanPostProcessor implements BeanPostProcessor {

    private Registry registry;
    private Transporter transporter;

    public ServiceBeanPostProcessor() {
        registry = TransportSupport.getRegistry();
        transporter = TransportSupport.getTransporter();
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Reference.class)) {
            // Get Reference annotation
            Reference service = bean.getClass().getAnnotation(Reference.class);
            // Build rpc service properties
            ServiceConfig serviceConfig = ServiceConfig.builder()
                    .group(service.group())
                    .version(service.version())
                    .service(bean).build();
            ApplicationModel.defaultModel().getApplicationConfigManager().addService(serviceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] services = targetClass.getDeclaredFields();
        for (Field service : services) {
            Reference rpcReference = service.getAnnotation(Reference.class);
            if (rpcReference != null) {
                ServiceConfig serviceConfig = ServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                ProxyFactory factory = TransportSupport.getProxyFactory();
                Invoker<Object> invoker = factory.getInvoker(, service.getClass());
                // Get proxy
                Object proxy = null;
                try {
                    proxy = factory.getProxy(invoker);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // Call the proxy method
                service.setAccessible(true);
                try {
                    service.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }

}
