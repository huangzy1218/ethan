package com.ethan.config;

import com.ethan.config.annotation.Reference;
import com.ethan.rpc.model.ApplicationModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class ServiceBeanPostProcessor implements BeanPostProcessor {

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
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Reference rpcReference = declaredField.getAnnotation(Reference.class);
            if (rpcReference != null) {
                ServiceConfig rpcServiceConfig = ServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }

}
