package com.ethan.config.annotation;

import com.ethan.common.URL;
import com.ethan.config.support.TransportSupport;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
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

    @Override
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
                try {
                    Object proxy = proxyFactory.getProxy(invoker);
                    // Set the proxy object into the field
                    field.setAccessible(true);
                    field.set(bean, proxy);
                } catch (Exception e) {
                    log.error("Failed to create or inject proxy for field '{}' in bean '{}'.", field.getName(),
                            bean.getClass().getName(), e);
                    throw new RuntimeException(e);
                }

            }
        }

        return bean;
    }

    private URL buildServiceURL(Field bean) {
        // Get Reference annotation
        Reference reference = bean.getAnnotation(Reference.class);
        // Build url
        URL url = URL.buildFixedURL();
        url.addParameter(GROUP_KEY, reference.group())
                .addParameter(VERSION_KEY, reference.version())
                .addParameter(TIMEOUT_KEY, reference.timeout())
                .addParameter(ASYNC_KEY, reference.async());
        return url;
    }

}
