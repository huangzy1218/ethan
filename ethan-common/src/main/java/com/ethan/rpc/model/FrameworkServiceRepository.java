package com.ethan.rpc.model;

import com.ethan.common.util.ConcurrentHashMapUtils;
import com.ethan.rpc.descriptor.ReflectionServiceDescriptor;
import com.ethan.rpc.descriptor.ServiceDescriptor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service repository for framework.
 *
 * @author Huang Z.Y.
 */
public class FrameworkServiceRepository {

    /**
     * Services.
     */
    private final ConcurrentMap<String, List<ServiceDescriptor>> services = new ConcurrentHashMap<>();


    /**
     * Consumers (key - group/interface:version value - consumerModel list).
     */
    private final ConcurrentMap<String, List<ConsumerModel>> consumers = new ConcurrentHashMap<>();

    /**
     * Providers.
     */
    private final ConcurrentMap<String, ProviderModel> providers = new ConcurrentHashMap<>();


    public void registerConsumer(ConsumerModel consumerModel) {
        ConcurrentHashMapUtils.computeIfAbsent(
                        consumers, consumerModel.getServiceKey(), (serviceKey) -> new CopyOnWriteArrayList<>())
                .add(consumerModel);
    }


    public void registerProvider(ProviderModel providerModel) {
        providers.putIfAbsent(providerModel.getServiceKey(), providerModel);
    }

    public ServiceDescriptor registerService(ServiceDescriptor serviceDescriptor) {
        return registerService(serviceDescriptor.getServiceInterfaceClass(), serviceDescriptor);
    }

    public ServiceDescriptor registerService(Class<?> interfaceClazz) {
        ServiceDescriptor serviceDescriptor = new ReflectionServiceDescriptor(interfaceClazz);
        return registerService(interfaceClazz, serviceDescriptor);
    }

    public ServiceDescriptor registerService(Class<?> interfaceClazz, ServiceDescriptor serviceDescriptor) {
        List<ServiceDescriptor> serviceDescriptors = ConcurrentHashMapUtils.computeIfAbsent(
                services, interfaceClazz.getName(), k -> new CopyOnWriteArrayList<>());
        synchronized (serviceDescriptors) {
            Optional<ServiceDescriptor> previous = serviceDescriptors.stream()
                    .filter(s -> s.getServiceInterfaceClass().equals(interfaceClazz))
                    .findFirst();
            if (previous.isPresent()) {
                return previous.get();
            } else {
                serviceDescriptors.add(serviceDescriptor);
                return serviceDescriptor;
            }
        }
    }

}
    