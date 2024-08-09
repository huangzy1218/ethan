package com.ethan.config.annotation;

import com.ethan.common.URL;
import com.ethan.config.ServiceConfig;
import com.ethan.config.support.TransportSupport;
import com.ethan.remoting.ServiceRepository;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
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
public class ReferenceAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ServiceRepository repository;

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
                Class<?> serviceType = field.getType();
                // Obtain the proxy factory and protocol
                ProxyFactory proxyFactory = TransportSupport.getProxyFactory();
                // Create an Invoker for the service
                URL url = buildServiceURL(field);
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
    private <T> ServiceConfig<T> buildServiceConfig(Object bean) {
        // Get Reference annotation
        Service service = bean.getClass().getAnnotation(Service.class);
        // Build rpc service properties
        ServiceConfig<T> serviceConfig = new ServiceConfig<>();
        serviceConfig.setGroup(service.group());
        serviceConfig.setVersion(service.version());
        serviceConfig.setAsync(service.async());
        serviceConfig.setProxy(service.proxy());
        serviceConfig.setInterfaceClass(bean.getClass().getInterfaces()[0]);
        serviceConfig.setInterfaceName(bean.getClass().getInterfaces()[0].getName());
        T ref = (T) bean;
        serviceConfig.setRef(ref);
        return serviceConfig;
    }

    private URL buildServiceURL(Field bean) {
        // Get Reference annotation
        Reference reference = bean.getAnnotation(Reference.class);
        // Build url
        URL url = URL.buildFixedURL();
        url.setServiceKey(bean.getClass().getName());
        url.addParameter(GROUP_KEY, reference.group())
                .addParameter(VERSION_KEY, reference.version())
                .addParameter(TIMEOUT_KEY, reference.timeout())
                .addParameter(ASYNC_KEY, reference.async());
        return url;
    }

}
