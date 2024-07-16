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

    /**
     * Checks if a string is null or empty.
     *
     * @param str The string to check
     * @return {@code true} if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String camelToSplitName(String camelName, String split) {
        if (isEmpty(camelName)) {
            return camelName;
        }
        if (!isWord(camelName)) {
            // Convert Ab-Cd-Ef to ab-cd-ef
            if (isSplitCase(camelName, split.charAt(0))) {
                return camelName.toLowerCase();
            }
            // not camel case
            return camelName;
        }

        StringBuilder buf = null;
        for (int i = 0; i < camelName.length(); i++) {
            char ch = camelName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (buf == null) {
                    buf = new StringBuilder();
                    if (i > 0) {
                        buf.append(camelName, 0, i);
                    }
                }
                if (i > 0) {
                    buf.append(split);
                }
                buf.append(Character.toLowerCase(ch));
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        return buf == null ? camelName.toLowerCase() : buf.toString().toLowerCase();
    }

    private static boolean isWord(String str) {
        if (str == null) {
            return false;
        }
        return str.chars().allMatch(ch -> isWord((char) ch));
    }

    private static boolean isSplitCase(String str, char separator) {
        if (str == null) {
            return false;
        }
        return str.chars().allMatch(ch -> (ch == separator) || isWord((char) ch));
    }
    
    private static boolean isWord(char ch) {
        if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
            return true;
        }
        return false;
    }

}
