package com.ethan.serialize.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author Huang Z.Y.
 */
public class JsonSerialization {

    public String serialize(Object obj) {
        if (obj == null) {
            return "null";
        }

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true); // Make private fields accessible
            String key = fields[i].getName();
            Object value;
            try {
                value = fields[i].get(obj);
            } catch (IllegalAccessException e) {
                value = "null";
            }
            jsonBuilder.append("\"").append(key).append("\":");
            jsonBuilder.append(serializeValue(value));

            if (i < fields.length - 1) {
                jsonBuilder.append(", ");
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    private String serializeValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        if (value.getClass().isArray()) {
            return serializeArray(value);
        }
        if (value instanceof Collection) {
            return serializeCollection((Collection<?>) value);
        }
        return value.toString(); // For primitives and other types
    }

    private String serializeArray(Object array) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            jsonBuilder.append(serializeValue(Array.get(array, i)));
            if (i < length - 1) {
                jsonBuilder.append(", ");
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    private String serializeCollection(Collection<?> collection) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        int size = collection.size();
        int i = 0;
        for (Object item : collection) {
            jsonBuilder.append(serializeValue(item));
            if (i < size - 1) {
                jsonBuilder.append(", ");
            }
            i++;
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

}
    