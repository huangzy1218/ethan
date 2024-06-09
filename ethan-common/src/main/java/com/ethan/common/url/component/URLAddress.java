package com.ethan.common.url.component;

import lombok.Data;

/**
 * A class which address parameters for {@link com.ethan.common.URL}.
 *
 * @author Huang Z,Y.
 */
@Data
public class URLAddress {

    private String protocol;
    private String host;
    private int port;
    private String interfaceName;
    private URLParam urlParam;

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

    public void addParam(String key, String value) {
        urlParam.addParameter(key, value);
    }

    public String getServiceKey() {
        return urlParam.getServiceKey(interfaceName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(host).append(":").append(port).append("/").append(interfaceName);
        String paramString = urlParam.toString();
        if (!paramString.equals("{}")) {
            sb.append("?").append(paramString.substring(1, paramString.length() - 1).replace(", ", "&"));
        }
        return sb.toString();
    }

}
