package com.ethan.common.url.component;

import com.ethan.rpc.model.ApplicationModel;

import java.util.*;

/**
 * Global param cache table.
 *
 * @author Huang Z,Y.
 */
public class DynamicParamTable {

    private static final Map<String, Integer> KEY2INDEX = new HashMap<>(64);
    /**
     * Keys array, value is string
     */
    private static String[] ORIGIN_KEYS;
    private static ParamValue[] VALUES;

    static {
        init();
    }

    private DynamicParamTable() {
        throw new IllegalStateException();
    }

    public static int getKeyIndex(String key) {
        Integer indexFromMap = KEY2INDEX.get(key);
        return indexFromMap == null ? -1 : indexFromMap;
    }

    public static int getValueIndex(String key, String value) {
        int idx = getKeyIndex(key);
        if (idx < 0) {
            throw new IllegalArgumentException("Cannot found key in url param:" + key);
        }
        ParamValue paramValue = VALUES[idx];
        return paramValue.getIndex(value);
    }

    public static String getKey(int offset) {
        return ORIGIN_KEYS[offset];
    }

    public static String getValue(int vi, int offset) {
        return VALUES[vi].getN(offset);
    }

    private static void init() {
        List<String> keys = new LinkedList<>();
        List<ParamValue> values = new LinkedList<>();
        Map<String, Integer> key2Index = new HashMap<>(64);
        keys.add("");
        values.add(new ParamValue(null));

        ApplicationModel.defaultModel()
                .getExtensionLoader(DynamicParamSource.class)
                .getSupportedExtensionInstances()
                .forEach(source -> source.init(keys, values));

        TreeMap<String, ParamValue> resultMap = new TreeMap<>(Comparator.comparingInt(System::identityHashCode));
        for (int i = 0; i < keys.size(); i++) {
            resultMap.put(keys.get(i), values.get(i));
        }

        ORIGIN_KEYS = resultMap.keySet().toArray(new String[0]);

        VALUES = resultMap.values().toArray(new ParamValue[0]);

        for (int i = 0; i < ORIGIN_KEYS.length; i++) {
            key2Index.put(ORIGIN_KEYS[i], i);
        }
        KEY2INDEX.putAll(key2Index);
    }

}
