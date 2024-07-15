package com.ethan.rpc.support;

import com.ethan.common.URL;
import com.ethan.rpc.Invocation;

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
        int timeout = Integer.parseInt(url.getParameter("timeout"));
        return Math.min(defaultTimeout, timeout);
    }

    public static String getMethodName(Invocation invocation) {
        return invocation.getMethodName();
    }

}
    