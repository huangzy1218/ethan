package com.ethan.common.config;

import com.ethan.common.util.StringUtils;
import lombok.Builder;
import lombok.Data;

/**
 * Abstract default configuration.
 *
 * @author Huang Z.Y.
 */
@Data
@Builder
public class AbstractInterfaceConfig extends AbstractConfig {

    private static final long serialVersionUID = -1035807325876392990L;

    /**
     * The remote service version the customer/provider side will reference.
     */
    protected String version;
    /**
     * The remote service group the customer/provider side will reference.
     */
    protected String group;
    /**
     * Strategies for generating dynamic agents.
     */
    protected String proxy;
    protected String serviceKey;

    private static String buildServiceKey(String path, String group, String version) {
        int length = path == null ? 0 : path.length();
        length += group == null ? 0 : group.length();
        length += version == null ? 0 : version.length();
        length += 2;
        StringBuilder buf = new StringBuilder(length);
        if (StringUtils.isNotEmpty(group)) {
            buf.append(group).append('/');
        }
        buf.append(path);
        if (StringUtils.isNotEmpty(version)) {
            buf.append(':').append(version);
        }
        return buf.toString().intern();
    }

    public void generateServiceKey() {
        this.serviceKey = buildServiceKey(interfaceName, group, version);
    }

}
    