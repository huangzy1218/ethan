package com.ethan.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * Miscellaneous collection utility methods.
 *
 * @author Huang Z,Y.
 */
public class CollectionUtils {

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Return {@code true} if the supplied Collection is {@code not null} or not empty.
     * Otherwise, return {@code false}.
     *
     * @param collection The Collection to check
     * @return Whether the given Collection is not empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param map The Map to check
     * @return Whether the given Map is empty
     */
    public static boolean isEmptyMap(Map map) {
        return map == null || map.size() == 0;
    }

    /**
     * Return {@code true} if the supplied Map is {@code not null} or not empty.
     * Otherwise, return {@code false}.
     *
     * @param map The Map to check
     * @return Whether the given Map is not empty
     */
    public static boolean isNotEmptyMap(Map map) {
        return !isEmptyMap(map);
    }

}
