package com.ethan.common;

import com.ethan.common.url.component.URLAddress;
import com.ethan.common.url.component.URLParam;
import com.ethan.common.util.StringUtils;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.ethan.common.constant.CommonConstants.CATEGORY_KEY;
import static com.ethan.common.constant.CommonConstants.GROUP_KEY;

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
public class URL implements Serializable {

    private static final long serialVersionUID = -4205514094465803451L;

    private final URLAddress urlAddress;
    private final URLParam urlParam;
    protected volatile Map<String, Object> attributes;
    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    private transient volatile String serviceKey;

    public URL(String protocol, String host, int port, String interfaceName) {
        this.urlAddress = new URLAddress(protocol, host, port, interfaceName);
        this.urlParam = new URLParam();
        this.attributes = new HashMap<>();
        serviceKey = interfaceName;
    }

    public URL(URLAddress urlAddress, URLParam urlParam) {
        this.urlAddress = urlAddress;
        this.urlParam = urlParam;
        serviceKey = urlAddress.getServiceKey();
    }

    /**
     * Parse decoded url string, formatted dubbo://host:port/path?param=value, into strutted URL.
     *
     * @param url, Decoded url string
     * @return
     */
    public static URL valueOf(String url) {
        return parseEncodedStr(url);
    }

    public static URL parseEncodedStr(String url) {
        int protocolEndIndex = url.indexOf("://");
        String protocol;
        if (protocolEndIndex == -1) {
            protocol = "exchange";
        } else {
            protocol = url.substring(0, protocolEndIndex);
        }

        String remainingUrl = url.substring(protocolEndIndex + 3);

        // Extract host and port
        int hostPortEndIndex = remainingUrl.indexOf('?');
        String hostPortAndPath;
        String paramString = "";
        if (hostPortEndIndex == -1) {
            hostPortAndPath = remainingUrl;
        } else {
            hostPortAndPath = remainingUrl.substring(0, hostPortEndIndex);
            paramString = remainingUrl.substring(hostPortEndIndex + 1);
        }

        // Extract host, port, and interface
        int pathStartIndex = hostPortAndPath.indexOf('/');
        String hostPort;
        String interfaceName = "";
        if (pathStartIndex == -1) {
            hostPort = hostPortAndPath;
        } else {
            hostPort = hostPortAndPath.substring(0, pathStartIndex);
            interfaceName = hostPortAndPath.substring(pathStartIndex);
        }

        String[] hostPortArray = hostPort.split(":");
        String host = hostPortArray[0];
        int port = (hostPortArray.length > 1) ? Integer.parseInt(hostPortArray[1]) : -1;

        URLAddress urlAddress = new URLAddress(protocol, host, port, interfaceName);

        // Extract parameters
        URLParam urlParam = new URLParam();
        if (!paramString.isEmpty()) {
            String[] paramPairs = paramString.split("&");
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    urlParam.addParameter(key, value);
                }
            }
        }

        return new URL(urlAddress, urlParam);
    }

    public void addParameter(String key, String value) {
        urlParam.addParameter(key, value);
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

    public boolean getParameter(String key, boolean defaultValue) {
        String value = getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(urlAddress.getProtocol()).append("://")
                .append(urlAddress.getHost()).append(":")
                .append(urlAddress.getPort());
        if (StringUtils.isNotEmpty(urlAddress.getInterfaceName())) {
            sb.append("/").append(urlAddress.getInterfaceName());
        }
        sb.append(getParamString());
        return sb.toString();
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    public int getParameter(String key, int defaultValue) {
        String value = getParameter(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public String getParameter(String key) {
        return urlParam.getParam(key);
    }

    public String getHost() {
        return urlAddress.getHost();
    }

    public int getPort() {
        return urlAddress.getPort();
    }

    public Map<String, Object> getAttributes() {
        return attributes == null ? Collections.emptyMap() : attributes;
    }

    public URL addAttributes(Map<String, Object> attributeMap) {
        if (attributeMap != null) {
            attributes.putAll(attributeMap);
        }
        return this;
    }

    public Object getAttribute(String key) {
        return attributes == null ? null : attributes.get(key);
    }

    public int getPositiveParameter(String key, int defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        int value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public void addParameter(String key, int value) {
        urlParam.addParameter(key, value);
    }

    public String getGroup() {
        return getParameter(GROUP_KEY);
    }

    public String getGroup(String defaultValue) {
        String value = getGroup();
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public String getAddress() {
        return urlAddress == null ? null : urlAddress.getAddress();
    }

    public String getServiceInterface() {
        return serviceKey;
    }

    public String getCategory(String defaultValue) {
        return getParameter(CATEGORY_KEY, defaultValue);
    }

    public String toFullString() {
        return getServiceInterface() + getParamString();
    }

    private String getParamString() {
        StringBuilder sb = new StringBuilder();
        if (urlParam != null && !urlParam.toString().equals("{}")) {
            String paramString = urlParam.toString();
            sb.append("?").append(paramString.substring(1, paramString.length() - 1).replace(", ", "&"));
        }

        return sb.toString();
    }

    public String getCategory() {
        return getParameter(CATEGORY_KEY);
    }

    public String[] getCategory(String[] defaultValue) {
        String value = getCategory();
        return StringUtils.isEmpty(value) ? defaultValue : COMMA_SPLIT_PATTERN.split(value);
    }

}
