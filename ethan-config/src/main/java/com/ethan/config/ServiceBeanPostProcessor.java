package com.ethan.config;

import com.ethan.config.annotation.Reference;
import com.ethan.model.ApplicationModel;
import com.ethan.registry.Registry;
import com.ethan.remoting.Transporter;
import com.ethan.rpc.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static com.ethan.common.constant.CommonConstants.ETHAN_PROTOCOL;
import static com.ethan.common.constant.CommonConstants.LOCAL_PROTOCOL;

/**
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class ServiceBeanPostProcessor<T extends Object> implements BeanPostProcessor {

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
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Get all fields of the bean class
        Class<T> targetClass = (Class<T>) bean.getClass();
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            // Check if the field has the @Reference annotation
            Reference rpcReference = field.getAnnotation(Reference.class);
            if (rpcReference != null) {
                Class<T> serviceType = (Class<T>) field.getType().getSuperclass();

                if (serviceType == null) {
                    throw new RpcException("Unable to determine service type for field: " + field.getName());
                }

                // Obtain the proxy factory and protocol
                ProxyFactory proxyFactory = TransportSupport.getProxyFactory();
                Protocol protocol = ApplicationModel.defaultModel().getExtensionLoader(Protocol.class).getExtension(ETHAN_PROTOCOL);

                // Create an Invoker for the service
                Invoker<T> invoker = proxyFactory.getInvoker((T) bean, serviceType);

                // Export the service
                Exporter<T> exporter = protocol.export(invoker);

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
