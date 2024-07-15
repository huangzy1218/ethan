package com.ethan.common.util;

import com.ethan.common.URL;

import static com.ethan.common.constant.CommonConstants.THREAD_NAME_KEY;

/**
 * Executor utility.
 *
 * @author Huang Z.Y.
 */
public class ExecutorUtils {

    /**
     * Append thread name with url address.
     *
     * @return Thread name
     */
    public static String setThreadName(URL url, String defaultName) {
        String name = url.getParameter(THREAD_NAME_KEY, defaultName);
        name = name + "-" + url.getAddress();
        return name;
    }

}
