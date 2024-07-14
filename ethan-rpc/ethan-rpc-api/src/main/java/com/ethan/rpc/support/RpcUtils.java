package com.ethan.rpc.support;

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

}
    