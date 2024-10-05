package com.ethan.common.util;

import com.ethan.common.URL;

import static com.ethan.common.constant.CommonConstants.*;

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
    public static boolean isItemMatch(String pattern, String value) {
        if (StringUtils.isEmpty(pattern)) {
            return value == null;
        } else {
            return "*".equals(pattern) || pattern.equals(value);
        }
    }

    public static String serializationOrDefault(URL url) {
        if (url != null) {
            String serialization = url.getParameter("serialization");
            if (!StringUtils.isEmpty(serialization)) {
                return serialization;
            }
        }
        return DEFAULT_SERIALIZATION;
    }

    public static int getCloseTimeout(URL url) {
        String configuredCloseTimeout = System.getProperty(CLOSE_TIMEOUT_CONFIG_KEY);
        int defaultCloseTimeout = -1;
        if (StringUtils.isNotEmpty(configuredCloseTimeout)) {
            try {
                defaultCloseTimeout = Integer.parseInt(configuredCloseTimeout);
            } catch (NumberFormatException e) {
                // use default heartbeat
            }
        }
        if (defaultCloseTimeout < 0) {
            defaultCloseTimeout = getIdleTimeout(url);
        }
        int closeTimeout = url.getParameter(CLOSE_TIMEOUT_KEY, defaultCloseTimeout);
        int heartbeat = getHeartbeat(url);
        if (closeTimeout < heartbeat * 2) {
            throw new IllegalStateException("closeTimeout < heartbeatInterval * 2");
        }
        return closeTimeout;
    }

    public static int getIdleTimeout(URL url) {
        int heartBeat = getHeartbeat(url);
        // idleTimeout should be at least more than twice heartBeat because possible retries of client.
        int idleTimeout = url.getParameter(HEARTBEAT_TIMEOUT_KEY, heartBeat * 3);
        if (idleTimeout < heartBeat * 2) {
            throw new IllegalStateException("idleTimeout < heartbeatInterval * 2");
        }
        return idleTimeout;
    }

    public static int getHeartbeat(URL url) {
        String configuredHeartbeat = System.getProperty(HEARTBEAT_CONFIG_KEY);
        int defaultHeartbeat = DEFAULT_HEARTBEAT;
        if (StringUtils.isNotEmpty(configuredHeartbeat)) {
            try {
                defaultHeartbeat = Integer.parseInt(configuredHeartbeat);
            } catch (NumberFormatException e) {
                // use default heartbeat
            }
        }
        return url.getParameter(HEARTBEAT_KEY, defaultHeartbeat);
    }
    
}
