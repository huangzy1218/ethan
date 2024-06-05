package com.ethan.common.util;

/**
 * Runtime utility.
 *
 * @author Huang Z.Y.
 */
public class RuntimeUtils {
    
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }

}
    