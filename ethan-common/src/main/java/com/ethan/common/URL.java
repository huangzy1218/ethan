package com.ethan.common;

import com.ethan.common.url.component.URLAddress;
import com.ethan.common.url.component.URLParam;

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

    // todo
//    /**
//     * Get value of specified key in URLParam.
//     *
//     * @param key Specified key
//     * @return Value, null if key is absent
//     */
//    public String getParameter(String key) {
//        int keyIndex = DynamicParamTable.getKeyIndex(enableCompressed, key);
//        if (keyIndex < 0) {
//            return EXTRA_PARAMS.get(key);
//        }
//        if (KEY.get(keyIndex)) {
//            String value;
//            int offset = keyIndexToOffset(keyIndex);
//            value = DynamicParamTable.getValue(keyIndex, offset);
//
//            return value;
//            //            if (StringUtils.isEmpty(value)) {
//            //                // Forward compatible, make sure key dynamic increment can work.
//            //                // In that case, some values which are proceed before increment will set in EXTRA_PARAMS.
//            //                return EXTRA_PARAMS.get(key);
//            //            } else {
//            //                return value;
//            //            }
//        }
//        return null;
//    }
//
//    public int getParameter(String key, int defaultValue) {
//        String value = getParameter(key);
//        if (StringUtils.isEmpty(value)) {
//            return defaultValue;
//        }
//        return Integer.parseInt(value);
//    }
//
//    public String getParameter(String key) {
//        return urlParam.getParameter(key);
//    }
//
//
//    public int getPositiveParameter(String key, int defaultValue) {
//        if (defaultValue <= 0) {
//            throw new IllegalArgumentException("defaultValue <= 0");
//        }
//        int value = getParameter(key, defaultValue);
//        return value <= 0 ? defaultValue : value;
//    }

}
