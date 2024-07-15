package com.ethan.serialize.jdk;


import com.ethan.common.extension.ExtensionLoader;
import com.ethan.example.TestPojo;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class JavaSerializationTest {

    @Test
    void testReadString() throws IOException, ClassNotFoundException {
        Serialization serialization = new JavaSerialization();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        objectOutput.writeUTF("hello");
        objectOutput.flushBuffer();

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serialization.deserialize(inputStream);
        Assertions.assertEquals("hello", objectInput.readUTF());
    }

    @Test
    void testReadObject() throws IOException, ClassNotFoundException {
        Serialization serialization = new JavaSerialization();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        TestPojo pojo = new TestPojo("hello", new Date());
        objectOutput.writeObject(pojo);
        objectOutput.flushBuffer();

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serialization.deserialize(inputStream);
        Assertions.assertEquals(pojo, objectInput.readObject(TestPojo.class));
    }

    @Test
    void testReadObject2() throws Exception {
        ExtensionLoader<Serialization> extensionLoader =
                ApplicationModel.defaultModel().getExtensionLoader(Serialization.class);
        Serialization serialization = extensionLoader.getExtension("jdk");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        TestPojo pojo = new TestPojo("hello", new Date());
        objectOutput.writeObject(pojo);
        objectOutput.flushBuffer();

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serialization.deserialize(inputStream);
        Assertions.assertEquals(pojo, objectInput.readObject(TestPojo.class));

    }

}