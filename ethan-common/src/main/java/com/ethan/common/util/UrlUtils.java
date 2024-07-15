package com.ethan.common.util;

import com.ethan.common.URL;

import static com.ethan.common.constant.CommonConstants.INTERFACE_KEY;

/**
 * URL utility.
 *
 * @author Huang Z.Y.
 */
public class UrlUtils {

    public static boolean isServiceKeyMatch(URL pattern, URL value) {
        return pattern.getParameter(INTERFACE_KEY).equals(value.getParameter(INTERFACE_KEY))
                && isItemMatch(pattern.getGroup(), value.getGroup())
                && isItemMatch(pattern.getVersion(), value.getVersion());
    }

    /**
     * Check if the given value matches the given pattern. The pattern supports wildcard "*".
     *
     * @param pattern Pattern
     * @param value   Value
     * @return {@code true} if match otherwise false
     */
    static boolean isItemMatch(String pattern, String value) {
        if (StringUtils.isEmpty(pattern)) {
            return value == null;
        } else {
            return "*".equals(pattern) || pattern.equals(value);
        }
    }

}
