package com.ethan.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration representing various Java Runtime Environment (JRE) versions.<br/>
 * Usage:
 * <pre>
 *  JRE currentJre = JRE.currentVersion();
 * </pre>
 *
 * @author Huang Z.Y.
 */
@Slf4j
public enum JRE {

    JAVA_8,

    JAVA_9,

    JAVA_10,

    JAVA_11,

    JAVA_12,

    JAVA_13,

    JAVA_14,

    JAVA_15,

    JAVA_16,

    JAVA_17,

    JAVA_18,

    JAVA_19,

    JAVA_20,

    JAVA_21,

    JAVA_22,

    JAVA_23,

    OTHER;

    /**
     * Get current JRE version.
     *
     * @return JRE version
     */
    public static JRE currentVersion() {
        return VERSION;
    }

    private static final JRE VERSION = getJre();

    /**
     * Is current version.
     *
     * @return {@code true} if current version
     */
    public boolean isCurrentVersion() {
        return this == VERSION;
    }

    private static JRE getJre() {
        // Get java version from system property
        String version = System.getProperty("java.version");
        boolean isBlank = StringUtils.isBlank(version);
        if (isBlank) {
            log.debug("java.version is blank");
        }
        // If start with 1.8 return java 8
        if (!isBlank && version.startsWith("1.8")) {
            return JAVA_8;
        }
        // If JRE version is 9 or above, we can get version from java.lang.Runtime.version()
        try {
            Object javaRunTimeVersion = MethodUtils.invokeMethod(Runtime.getRuntime(), "version");
            int majorVersion = MethodUtils.invokeMethod(javaRunTimeVersion, "major");
            switch (majorVersion) {
                case 9:
                    return JAVA_9;
                case 10:
                    return JAVA_10;
                case 11:
                    return JAVA_11;
                case 12:
                    return JAVA_12;
                case 13:
                    return JAVA_13;
                case 14:
                    return JAVA_14;
                case 15:
                    return JAVA_15;
                case 16:
                    return JAVA_16;
                case 17:
                    return JAVA_17;
                case 18:
                    return JAVA_18;
                case 19:
                    return JAVA_19;
                case 20:
                    return JAVA_20;
                case 21:
                    return JAVA_21;
                case 22:
                    return JAVA_22;
                default:
                    return OTHER;
            }
        } catch (Exception e) {
            log.debug("Can't determine current JRE version (maybe java.version is null), " +
                    "assuming that JRE version is 8.", e);
        }
        // Default java 8
        return JAVA_8;
    }

}
