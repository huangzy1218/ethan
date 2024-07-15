package com.ethan.rpc.support;

import com.ethan.common.URL;
import com.ethan.common.util.StringUtils;
import com.ethan.rpc.Invocation;

import static com.ethan.common.constant.CommonConstants.TIMEOUT_KEY;

/**
 * @author Huang Z.Y.
 */
public class RpcUtils {

    public static Long convertToNumber(Object obj, long defaultTimeout) {
        Long timeout = convertToNumber(obj);
        return timeout == null ? defaultTimeout : timeout;
    }

    public static Long convertToNumber(Object obj) {
        Long timeout = null;
        try {
            if (obj instanceof String) {
                timeout = Long.parseLong((String) obj);
            } else if (obj instanceof Number) {
                timeout = ((Number) obj).longValue();
            } else {
                timeout = Long.parseLong(obj.toString());
            }
        } catch (Exception e) {
            // ignore
        }
        return timeout;
    }

    public static int calculateTimeout(URL url, int defaultTimeout) {
        String timeout = url.getParameter(TIMEOUT_KEY);
        if (StringUtils.isNotEmpty(timeout)) {
            return Math.min(Integer.parseInt(timeout), defaultTimeout);
        }
        return defaultTimeout;
    }

    public static String getMethodName(Invocation invocation) {
        return invocation.getMethodName();
    }

}
    