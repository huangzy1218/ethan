package com.ethan.common;

import com.ethan.common.url.component.URLAddress;
import com.ethan.common.url.component.URLParam;
import com.ethan.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * URL (Uniform Resource Locator).<br/>
 * URL examples:
 * <ul>
 *     <li>http://www.facebook.com/friends?param1=value1&amp;param2=value2</li>
 *     <li>registry://192.168.1.7:9090/org.apache.dubbo.service1?param1=value1&amp;param2=value2</li>
 * </ul>
 *
 * @author Huang Z.Y.
 */
@Component
public class URL implements Serializable {

    @Serial
    private static final long serialVersionUID = 2691386627L;

    private final URLAddress urlAddress;
    private final URLParam urlParam;

    private transient volatile String serviceKey;
    protected volatile Map<String, Object> attributes;


    public URL(String protocol, String host, int port, String interfaceName) {
        this.urlAddress = new URLAddress(protocol, host, port, interfaceName);
        this.urlParam = new URLParam();
        this.attributes = new HashMap<>();
    }

    public void addParam(String key, String value) {
        urlParam.addParam(key, value);
        // Invalidate cached service key
        this.serviceKey = null;
    }

    public String getServiceKey() {
        if (serviceKey == null) {
            synchronized (this) {
                if (serviceKey == null) {
                    serviceKey = urlParam.getServiceKey(urlAddress.getInterfaceName());
                }
            }
        }
        return serviceKey;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(urlAddress.toString());
        String paramString = urlParam.toString();
        if (!paramString.equals("{}")) {
            sb.append("?").append(paramString.substring(1, paramString.length() - 1).replace(", ", "&"));
        }
        return sb.toString();
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    public String getParameter(String key) {
        return urlParam.getParam(key);
    }


    public int getPositiveParameter(String key, int defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        int value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public String getHost() {
        return urlAddress == null ? null : urlAddress.getHost();
    }

    public int getPort() {
        return urlAddress == null ? 0 : urlAddress.getPort();
    }

}
