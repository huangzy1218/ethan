package com.ethan.config;

import com.ethan.common.config.AbstractInterfaceConfig;
import com.ethan.common.config.ConsumerConfig;
import com.ethan.common.constant.CommonConstants;
import com.ethan.common.util.StringUtils;
import com.ethan.rpc.EthanStub;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.descriptor.ServiceDescriptor;
import com.ethan.rpc.model.ConsumerModel;
import com.ethan.rpc.model.FrameworkServiceRepository;

import java.util.Map;

/**
 * @author Huang Z.Y.
 */
public class ReferenceConfig<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 1505896195123513822L;

    /**
     * The interface class of the reference service.
     */
    protected Class<?> interfaceClass;
    /**
     * The url for peer-to-peer invocation.
     */
    protected String url;
    /**
     * The consumer config (default).
     */
    protected ConsumerConfig consumer;
    private ConsumerModel consumerModel;
    /**
     * The interface proxy reference.
     */
    private transient volatile T ref;
    /**
     * The invoker of the reference service.
     */
    private transient volatile Invoker<?> invoker;
    /**
     * whether this ReferenceConfig has been destroyed.
     */
    private transient volatile boolean destroyed;
    /**
     * The flag whether the ReferenceConfig has been initialized.
     */
    private transient volatile boolean initialized;


    public T get() {
        if (destroyed) {
            throw new IllegalStateException("The invoker of ReferenceConfig(" + url + ") has already destroyed!");
        }

        if (ref == null) {
            init();
        }

        return ref;
    }

    protected synchronized void init() {
        if (initialized && ref != null) {
            return;
        }
        try {
            // Auto detect proxy type
            String proxyType = getProxy();
            if (StringUtils.isBlank(proxyType) && EthanStub.class.isAssignableFrom(interfaceClass)) {
                setProxy(CommonConstants.NATIVE_STUB);
            }

            Map<String, String> referenceParameters = appendConfig();

            FrameworkServiceRepository repository = getScopeModel().getServiceRepository();
            ServiceDescriptor serviceDescriptor;
            if (CommonConstants.NATIVE_STUB.equals(getProxy())) {
                serviceDescriptor = StubSuppliers.getServiceDescriptor(interfaceName);
                repository.registerService(serviceDescriptor);
                setInterface(serviceDescriptor.getInterfaceName());
            } else {
                serviceDescriptor = repository.registerService(interfaceClass);
            }
            consumerModel = new ConsumerModel(proxy, serviceKey);

            // Compatible with dependencies on ServiceModel#getReferenceConfig() , and will be removed in a future
            // version.
            consumerModel.setConfig(this);

            repository.registerConsumer(consumerModel);

            serviceMetadata.getAttachments().putAll(referenceParameters);

            ref = createProxy(referenceParameters);

            serviceMetadata.setTarget(ref);
            serviceMetadata.addAttribute(PROXY_CLASS_REF, ref);

            consumerModel.setProxyObject(ref);
            consumerModel.initMethodModels();

            if (check) {
                checkInvokerAvailable(0);
            }
        } catch (Throwable t) {
            logAndCleanup(t);

            throw t;
        }
        initialized = true;
    }

}
