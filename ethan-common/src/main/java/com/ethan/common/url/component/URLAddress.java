package com.ethan.common.url.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.ethan.common.constant.CommonConstants.ETHAN;

/**
 * A class which address parameters for {@link com.ethan.common.URL}.
 *
 * @author Huang Z,Y.
 */
@Data
@Slf4j
public class URLAddress {

    /**
     * Cache.
     */
    protected transient String rawAddress;
    private String protocol = ETHAN;
    private String host;
    private int port;
    private String interfaceName;
    private URLParam urlParam;

    public URLAddress() {
        host = getLocalAddress();
    }

    public URLAddress(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public URLAddress(String protocol, String host, int port, String interfaceName) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.interfaceName = interfaceName;
        this.urlParam = new URLParam();
    }

    public String getLocalAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            log.info("Failed to get local host address, returning 'localhost'. Cause: {}", e.getMessage(), e);
            return "localhost";
        }
    }

    public void addParam(String key, String value) {
        urlParam.addParameter(key, value);
    }

    public String getServiceKey() {
        return urlParam.getServiceKey(interfaceName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(host).append(":").append(port).append(interfaceName);
        String paramString = urlParam.toString();
        if (!paramString.equals("{}")) {
            sb.append("?").append(paramString.substring(1, paramString.length() - 1).replace(", ", "&"));
        }
        return sb.toString();
    }

    public String getAddress() {
        if (rawAddress == null) {
            rawAddress = getAddress(getHost(), getPort());
        }
        return rawAddress;
    }

    protected String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }

}
