package com.ethan.serialize.hessian2;

import com.ethan.example.TestPojo;
import com.ethan.serialize.api.ObjectInput;
import com.ethan.serialize.api.ObjectOutput;
import com.ethan.serialize.api.Serialization;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

class Hessian2SerializationTest {

    @Test
    void testReadString() throws IOException {
        Serialization serialization = new Hessian2Serialization();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        objectOutput.writeUTF("hello");
        objectOutput.flushBuffer();

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serialization.deserialize(inputStream);
        System.out.println(objectInput.readUTF());
    }

    @Test
    void testReadObject() throws IOException, ClassNotFoundException {
        Serialization serialization = new Hessian2Serialization();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        TestPojo pojo = new TestPojo("hello", new Date());
        objectOutput.writeObject(pojo);
        objectOutput.flushBuffer();

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serialization.deserialize(inputStream);
        System.out.println(objectInput.readObject());
    }

}