package com.ethan.config;

import com.ethan.rpc.model.ServiceMetadata;
import lombok.Data;

import java.io.Serializable;

/**
 * Abstract default configuration.
 *
 * @author Huang Z.Y.
 */
@Data
public class AbstractInterfaceConfig implements Serializable {

    private static final long serialVersionUID = -1035807325876392990L;

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
    /**
     * Strategies for generating dynamic agents.
     */
    protected String proxy;

    protected void initServiceMetadata(AbstractInterfaceConfig provider) {
        serviceMetadata.setVersion(version);
        serviceMetadata.setGroup(group);
        serviceMetadata.setServiceInterfaceName(interfaceName);
    }

}
    