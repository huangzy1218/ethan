package com.ethan.common.config;

import com.ethan.common.util.ConcurrentHashMapUtils;
import com.ethan.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility methods and public methods for parsing configuration.
 *
 * @author Huang Z.Y.
 */
public abstract class AbstractConfig implements Serializable {

    private static final long serialVersionUID = -3553540556580610367L;

    /**
     * Tag name cache, speed up get tag name frequently.
     */
    private static final ConcurrentMap<Class, String> tagNameCache = new ConcurrentHashMap<>();
    /**
     * The suffix container.
     */
    private static final String[] SUFFIXES = new String[]{"Config", "Bean", "ConfigBase"};
    /**
     * The config id.
     */
    @Getter
    @Setter
    private String id;

    public static String getTagName(Class<?> cls) {
        return ConcurrentHashMapUtils.computeIfAbsent(tagNameCache, cls, (key) -> {
            String tag = cls.getSimpleName();
            for (String suffix : SUFFIXES) {
                if (tag.endsWith(suffix)) {
                    tag = tag.substring(0, tag.length() - suffix.length());
                    break;
                }
            }
            return StringUtils.camelToSplitName(tag, "-");
        });
    }

}
