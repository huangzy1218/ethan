package com.ethan.common.extension;

import com.example.Greeting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExtensionLoaderTest {

    @Test
    public void testGetExtension() {
        ExtensionLoader<Greeting> loader = new ExtensionLoader<>(Greeting.class);

        Greeting englishGreeting = loader.getExtension("english");
        Assertions.assertNotNull(englishGreeting);
        Assertions.assertEquals("Hello", englishGreeting.greet());

        Greeting spanishGreeting = loader.getExtension("spanish");
        Assertions.assertNotNull(spanishGreeting);
        Assertions.assertEquals("Hola", spanishGreeting.greet());
    }

}