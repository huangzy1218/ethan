package com.ethan.common.javassist;

import com.example.Hello;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Huang Z.Y.
 */
class JavassistWrapperTest {

    @Test
    public void test() throws Throwable {
        JavassistWrapper wrapper = JavassistWrapper.getWrapper(Hello.class);
        Hello instance = new Hello();

        Object result = wrapper.invokeMethod(instance, "sayHello",
                new Class[]{String.class}, new Object[]{"World"});
        String exception = "Hello, World";
        Assertions.assertEquals(exception, result);
    }

}