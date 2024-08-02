package com.ethan.config;

import com.ethan.config.annotation.Reference;
import com.ethan.model.ApplicationModel;
import com.ethan.registry.Registry;
import com.ethan.remoting.Transporter;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Protocol;
import com.ethan.rpc.ProxyFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static com.ethan.common.constant.CommonConstants.LOCAL_PROTOCOL;

/**
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class ServiceBeanPostProcessor<T> implements BeanPostProcessor {

    private final Protocol protocol;
    private Registry registry;
    private Transporter transporter;
    private ServiceRepository repository;

    public ServiceBeanPostProcessor() {
        protocol = ApplicationModel.defaultModel()
                .getExtensionLoader(Protocol.class).getExtension(LOCAL_PROTOCOL);
        registry = TransportSupport.getRegistry();
        transporter = TransportSupport.getTransporter();
    }

    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Reference.class)) {
            ServiceConfig<?> serviceConfig = buildServiceConfig(bean);
            repository.registerService(serviceConfig);
        }
        return bean;
    }

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] services = targetClass.getDeclaredFields();
        for (Field service : services) {
            Reference rpcReference = service.getAnnotation(Reference.class);
            if (rpcReference != null) {
                ProxyFactory factory = TransportSupport.getProxyFactory();
                // Get service interface
                Class<?> superclass = service.getType().getSuperclass();
                Invoker<?> invoker = protocol.refer(superclass);
                // Get proxy
                try {
                    Object proxy = factory.getProxy(invoker);
                    service.setAccessible(true);
                    service.set(bean, proxy);
                } catch (Exception e) {
                    log.error("Can't inject service bean: {}, cause: {}", service.getName(), e.getMessage(), e);
                }
            }
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private ServiceConfig buildServiceConfig(Object bean) {
        // Get Reference annotation
        Reference service = bean.getClass().getAnnotation(Reference.class);
        // Build rpc service properties
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setGroup(service.group());
        serviceConfig.setVersion(service.version());
        serviceConfig.setAsync(service.async());
        serviceConfig.setProxy(service.proxy());
        serviceConfig.setInterfaceName(service.getClass().getName());
        serviceConfig.setRegistry(service.registry());
        T ref = (T) service;
        serviceConfig.setRef(ref);
        return serviceConfig;
    }

}
