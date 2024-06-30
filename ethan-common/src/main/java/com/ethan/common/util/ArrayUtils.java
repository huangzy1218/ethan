package com.ethan.common.util;

/**
 * @author Huang Z.Y.
 */
public class ArrayUtils {


    /**
     * Checks if the array is null or empty.
     *
     * @param array The array to check
     * @return {@code true} if the array is null or empty
     */
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if the array is not null or empty.
     *
     * @param array The array to check
     * @return {@code true} if the array is not null or empty
     */
    public static boolean isNotEmpty(final Object[] array) {
        return !isEmpty(array);
    }

}
    