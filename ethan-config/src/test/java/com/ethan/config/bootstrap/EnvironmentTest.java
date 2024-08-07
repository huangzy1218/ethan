package com.ethan.config.bootstrap;

import com.ethan.model.ApplicationModel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @author Huang Z.Y.
 */
public class EnvironmentTest {

    @Test
    public void testProperties() {
        Bootstrap ethan = Bootstrap.getInstance().application("ethan");
        ethan.initialize();
        Object timeout = ApplicationModel.defaultModel().modelEnvironment().getProperty("ethan.timeout");
        Object proxy = ApplicationModel.defaultModel().modelEnvironment().getProperty("ethan.proxy");
        Assertions.assertEquals(1000, timeout);
        Assertions.assertEquals("cglib", proxy);
    }

}
