package com.ethan.remoting.transport;

import com.ethan.common.URL;
import com.ethan.model.ApplicationModel;
import com.ethan.remoting.Codec;
import com.ethan.remoting.exchange.codec.ExchangeCodec;
import lombok.Getter;

import static com.ethan.common.constant.CommonConstants.CONNECT_TIMEOUT_KEY;
import static com.ethan.common.constant.CommonConstants.DEFAULT_CONNECT_TIMEOUT;

/**
 * @author Huang Z.Y.
 */
public class AbstractEndpoint {

    @Getter
    protected URL url;
    @Getter
    private Codec codec;
    @Getter
    private int connectTimeout;

    public AbstractEndpoint(URL url) {
        this.url = url;
        this.codec = getChannelCodec(url);
        this.connectTimeout =
                url.getPositiveParameter(CONNECT_TIMEOUT_KEY, DEFAULT_CONNECT_TIMEOUT);
    }

    protected static Codec getChannelCodec(URL url) {
        return ApplicationModel.defaultModel().getExtensionLoader(Codec.class).getExtension(ExchangeCodec.NAME);
    }

}
    