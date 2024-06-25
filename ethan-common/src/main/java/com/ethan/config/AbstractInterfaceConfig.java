package com.ethan.config;

import com.ethan.rpc.model.ServiceMetadata;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract default configuration.
 *
 * @author Huang Z.Y.
 */
@Data
public class AbstractInterfaceConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -1559314110797223229L;

    /**
     * The interface name of the exported service.
     */
    protected String interfaceName;

    /**
     * The remote service version the customer/provider side will reference.
     */
    protected String version;

    /**
     * The remote service group the customer/provider side will reference.
     */
    protected String group;

    protected ServiceMetadata serviceMetadata;
    
    protected void initServiceMetadata(AbstractInterfaceConfig provider) {
        serviceMetadata.setVersion(version);
        serviceMetadata.setGroup(group);
        serviceMetadata.setServiceInterfaceName(interfaceName);
    }

}
    