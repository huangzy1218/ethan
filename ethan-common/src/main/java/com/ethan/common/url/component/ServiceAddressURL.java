package com.ethan.common.url.component;

import com.ethan.common.URL;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * URL for configuration information on the consumer side.
 *
 * @author Huang Z.Y.
 */
public class ServiceAddressURL extends URL {

    protected final transient URL consumerURL;


    public ServiceAddressURL(URLAddress urlAddress, URLParam urlParam, URL consumerURL) {
        super(urlAddress, urlParam);
        this.consumerURL = consumerURL;
    }

    @Override
    public String getServiceInterface() {
        return consumerURL.getServiceInterface();
    }

    @Override
    public String getGroup() {
        return super.getParameter(GROUP_KEY);
    }

    @Override
    public String getVersion() {
        return super.getParameter(VERSION_KEY);
    }

    @Override
    public String getSide() {
        return CONSUMER_SIDE;
    }

}
