package com.ethan.common.util;

public class StringUtils {

    /**
     * Is empty string.
     *
     * @param str Source string
     * @return Is empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * If s1 is null and s2 is null, then return true.
     *
     * @param s1 str1
     * @param s2 str2
     * @return Equals
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    /**
     * If cs is blank, then return true.
     *
     * @param cs str
     * @return {@code true} if cs is blank
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is not empty string.
     *
     * @param str Source string
     * @return Is not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Checks if the array is null or empty.<br/>
     *
     * @param array The array to check
     * @return {@code true} if the array is null or empty
     */
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

}
