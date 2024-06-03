package com.ethan.registry;

import java.net.URL;
import java.util.List;

/**
 * Notify listener, which is a functional interface.
 *
 * @author Huang Z.Y.
 */
public interface NotifyListener {

    void notify(List<URL> urls);

}
