package com.ethan.common.config;

import com.ethan.common.util.StringUtils;

import static com.ethan.common.constant.CommonConstants.DEFAULT_SERIALIZATION;
import static com.ethan.common.constant.CommonConstants.ETHAN_PROTOCOL;

/**
 * Protocol configuration.
 *
 * @author Huang Z.Y.
 */
public class ProtocolConfig extends AbstractConfig {

    private static final long serialVersionUID = -3298984911642858706L;

    /**
     * Protocol name.
     */
    private String name;

    /**
     * Service ip address (when there are multiple network cards available).
     */
    private String host;

    /**
     * Service port.
     */
    private Integer port;

    /**
     * Context path.
     */
    private String contextPath;

    /**
     * Protocol codec.
     */
    private String codec;

    /**
     * The configuration supports multiple, which are separated by commas.
     * Such as: <code>fastjson2,fastjson,hessian2</code>
     */
    private String serialization;

    /**
     * Charset.
     */
    private String charset;

    public ProtocolConfig(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public ProtocolConfig(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    private void checkDefault() {
        if (name == null) {
            name = ETHAN_PROTOCOL;
        }

        if (StringUtils.isBlank(serialization)) {
            serialization = serialization != null
                    ? serialization
                    : DEFAULT_SERIALIZATION;
        }
    }

}
