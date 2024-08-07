package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.config.annotation.Reference;
import com.ethan.config.annotation.Service;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.RpcException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class ReferenceAnnotationBeanPostProcessor<T> implements BeanPostProcessor {

    private ServiceRepository repository;

    public ReferenceAnnotationBeanPostProcessor() {
        repository = new ServiceRepository();
    }

    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Service.class)) {
            ServiceConfig<?> serviceConfig = buildServiceConfig(bean);
            repository.registerService(serviceConfig);
        }
        return bean;
    }

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Get all fields of the bean class
        Class<?> targetClass = bean.getClass();
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            // Check if the field has the @Reference annotation
            Reference rpcReference = field.getAnnotation(Reference.class);
            if (rpcReference != null) {
                Class<?> serviceType = field.getType().getSuperclass();
                if (serviceType == null) {
                    throw new RpcException("Unable to determine service type for field: " + field.getName() +
                            ", it must implements a interface.");
                }
                // Obtain the proxy factory and protocol
                ProxyFactory proxyFactory = TransportSupport.getProxyFactory();
                // Create an Invoker for the service
                URL url = buildServiceUrl(bean);
                Invoker<?> invoker = proxyFactory.getInvoker(bean, serviceType, url, 1);
                // Create the proxy object
                Object proxy = proxyFactory.getProxy(invoker);
                // Set the proxy object into the field
                field.setAccessible(true);
                field.set(bean, proxy);
            }
        }

        return bean;
    }

    @SuppressWarnings("unchecked")
    private ServiceConfig<T> buildServiceConfig(Object bean) {
        // Get Reference annotation
        Service service = bean.getClass().getAnnotation(Service.class);
        // Build rpc service properties
        ServiceConfig<T> serviceConfig = new ServiceConfig<>();
        serviceConfig.setGroup(service.group());
        serviceConfig.setVersion(service.version());
        serviceConfig.setAsync(service.async());
        serviceConfig.setProxy(service.proxy());
        serviceConfig.setInterfaceName(service.getClass().getName());
        T ref = (T) service;
        serviceConfig.setRef(ref);
        return serviceConfig;
    }

    private URL buildServiceUrl(Object bean) {
        // Get Reference annotation
        Reference reference = bean.getClass().getAnnotation(Reference.class);
        // Build url
        URL url = URL.buildFixedURL();
        url.addParameter(GROUP_KEY, reference.group())
                .addParameter(VERSION_KEY, reference.version())
                .addParameter(TIMEOUT_KEY, reference.timeout())
                .addParameter(ASYNC_KEY, reference.async());
        return url;
    }

}
