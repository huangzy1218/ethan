package com.ethan.common.url.component;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * A class which store parameters for {@link com.ethan.common.URL}.
 *
 * @author Huang Z.Y.
 */
@Data
public class URLParam {

    private Map<String, String> parameters;

    public URLParam() {
        this.parameters = new HashMap<>();
    }

    public void addParam(String key, String value) {
        parameters.put(key, value);
    }

    public String getParam(String key) {
        return parameters.get(key);
    }

    public String getServiceKey(String interfaceName) {
        String group = parameters.get("group");
        String version = parameters.get("version");

        StringBuilder serviceKey = new StringBuilder();
        if (group != null && !group.isEmpty()) {
            serviceKey.append(group).append("/");
        }
        serviceKey.append(interfaceName);
        if (version != null && !version.isEmpty()) {
            serviceKey.append(":").append(version);
        }

        return serviceKey.toString();
    }

    @Override
    public String toString() {
        return parameters.toString();
    }

}
