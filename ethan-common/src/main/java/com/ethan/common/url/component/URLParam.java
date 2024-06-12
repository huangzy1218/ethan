package com.ethan.common.url.component;

import com.ethan.common.util.StringUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.ethan.common.constant.CommonConstants.GROUP_KEY;
import static com.ethan.common.constant.CommonConstants.VERSION_KEY;

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

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public void addParameter(String key, int value) {
        addParameter(key, String.valueOf(value));
    }


    public String getParam(String key) {
        return parameters.get(key);
    }

    public String getServiceKey(String interfaceName) {
        String group = parameters.get(GROUP_KEY);
        String version = parameters.get(VERSION_KEY);

        StringBuilder serviceKey = new StringBuilder();
        if (StringUtils.isNotEmpty(group)) {
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
