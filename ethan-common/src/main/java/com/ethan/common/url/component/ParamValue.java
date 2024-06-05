package com.ethan.common.url.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache param value.
 *
 * @author Huang Z.Y.
 */
public class ParamValue {

    private volatile String[] index2Value = new String[1];
    private final Map<String, Integer> value2Index = new ConcurrentHashMap<>();
    private int indexSeq = 0;

    public ParamValue(String defaultVal) {
        if (defaultVal == null) {
            indexSeq += 1;
        } else {
            add(defaultVal);
        }
    }


    public int add(String value) {
        Integer index = value2Index.get(value);
        if (index != null) {
            return index;
        } else {
            synchronized (this) {
                // Thread safe
                if (!value2Index.containsKey(value)) {
                    if (indexSeq == Integer.MAX_VALUE) {
                        throw new IllegalStateException("URL Param Cache is full.");
                    }
                    // Copy on write, only support append now
                    String[] newValues = new String[indexSeq + 1];
                    System.arraycopy(index2Value, 0, newValues, 0, indexSeq);
                    newValues[indexSeq] = value;
                    index2Value = newValues;
                    value2Index.put(value, indexSeq);
                    indexSeq += 1;
                }
            }
        }
        return value2Index.get(value);
    }

    /**
     * Get value at the specified index.
     *
     * @param n The nth value
     * @return The value stored at index = n
     */
    public String getN(int n) {
        if (n == -1) {
            return null;
        }
        return index2Value[n];
    }

    public int getIndex(String value) {
        if (value == null) {
            return -1;
        }
        Integer index = value2Index.get(value);
        if (index == null) {
            return add(value);
        }
        return index;
    }

}
