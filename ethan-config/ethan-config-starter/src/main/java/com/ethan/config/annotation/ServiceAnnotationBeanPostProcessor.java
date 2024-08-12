package com.ethan.config.annotation;

import com.ethan.common.context.ApplicationContextHolder;
import com.ethan.config.ServiceConfig;
import com.ethan.config.ServiceRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Huang Z.Y.
 */
@Component
@Slf4j
public class ServiceAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ServiceRepository repository;

    public ServiceAnnotationBeanPostProcessor() {
        repository = ApplicationContextHolder.getBean(ServiceRepository.class);
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

}
