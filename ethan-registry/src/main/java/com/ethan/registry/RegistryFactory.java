package com.ethan.registry;

import com.ethan.common.URL;
import com.ethan.common.extension.SPI;

/**
 * Registry factory.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface RegistryFactory {

    /**
     * Create registry by url.
     *
     * @param url Url
     * @return Registry instance
     */
    Registry createRegistry(URL url);

}
