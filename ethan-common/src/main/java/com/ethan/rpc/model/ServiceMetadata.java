package com.ethan.rpc.model;

import com.ethan.common.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Service data related to service level such as name, version etc,
 *
 * @author Huang Z.Y.
 */
@Data
@NoArgsConstructor
@ToString
public class ServiceMetadata {

    private String serviceKey;
    private String serviceInterfaceName;
    private String version;
    private volatile String group;
    private ServiceModel serviceModel;
    private Object target;

    public ServiceMetadata(String serviceInterfaceName, String group, String version) {
        this.serviceInterfaceName = serviceInterfaceName;
        this.group = group;
        this.version = version;
        this.serviceKey = buildServiceKey(serviceInterfaceName, group, version);
    }

    public static String buildServiceKey(String path, String group, String version) {
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
        this.serviceKey = buildServiceKey(serviceInterfaceName, group, version);
    }

}
