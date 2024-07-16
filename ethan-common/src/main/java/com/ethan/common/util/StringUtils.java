package com.ethan.common.util;

public class StringUtils {

    public static final int INDEX_NOT_FOUND = -1;

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

    /**
     * Test str whether starts with the prefix ignore case.
     *
     * @param str    String
     * @param prefix Prefix
     * @return {@code true} for success
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null || str.length() < prefix.length()) {
            return false;
        }
        // return str.substring(0, prefix.length()).equalsIgnoreCase(prefix);
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    protected static boolean isSnakeCase(String str) {
        return str.contains("_") || str.equals(str.toLowerCase()) || str.equals(str.toUpperCase());
    }

    /**
     * Convert snake_case or SNAKE_CASE to kebab-case.<br/>
     * NOTE: Return itself if it's not a snake case
     *
     * @param snakeName Snake name
     * @param split     Split string
     * @return Split string
     */
    public static String snakeToSplitName(String snakeName, String split) {
        String lowerCase = snakeName.toLowerCase();
        if (isSnakeCase(snakeName)) {
            return replace(lowerCase, "_", split);
        }
        return snakeName;
    }

    /**
     * <p>Replaces all occurrences of a String within another String.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("any", null, *)    = "any"
     * StringUtils.replace("any", *, null)    = "any"
     * StringUtils.replace("any", "", *)      = "any"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "b"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @return the text with any replacements processed,
     * {@code null} if null String input
     * @see #replace(String text, String searchString, String replacement, int max)
     */
    public static String replace(final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first {@code max} values of the search String.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("any", null, *, *)     = "any"
     * StringUtils.replace("any", *, null, *)     = "any"
     * StringUtils.replace("any", "", *, *)       = "any"
     * StringUtils.replace("any", *, *, 0)        = "any"
     * StringUtils.replace("abaa", "a", null, -1) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max          maximum number of values to replace, or {@code -1} if no maximum
     * @return the text with any replacements processed,
     * {@code null} if null String input
     */
    public static String replace(final String text, final String searchString, final String replacement, int max) {
        if (isAnyEmpty(text, searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    public static boolean isAnyEmpty(final String... ss) {
        return !isNoneEmpty(ss);
    }

    public static boolean isNoneEmpty(final String... ss) {
        if (ArrayUtils.isEmpty(ss)) {
            return false;
        }
        for (final String s : ss) {
            if (isEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert camelCase or snake_case/SNAKE_CASE to kebab-case.
     *
     * @param str
     * @param split
     * @return
     */
    public static String convertToSplitName(String str, String split) {
        if (isSnakeCase(str)) {
            return snakeToSplitName(str, split);
        } else {
            return camelToSplitName(str, split);
        }
    }

}
