package com.ethan.common.extension;

/**
 * Uniform accessor for extension.
 *
 * @author Huang Z.Y.
 */
public interface ExtensionAccessor {

    ExtensionDirector getExtensionDirector();

    default <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        return this.getExtensionDirector().getExtensionLoader(type);
    }
    
}
