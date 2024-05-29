package com.ethan.serialize.kryo;

import com.ethan.example.TestPojo;
import com.ethan.serialize.api.ObjectInput;
import com.ethan.serialize.api.ObjectOutput;
import com.ethan.serialize.api.Serialization;
import com.ethan.serialize.kryo.util.KryoThreadLocalUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

class KryoSerializationTest {

    @Test
    void testReadString() throws IOException, ClassNotFoundException {
        Serialization serialization = new KryoSerialization();
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
        KryoThreadLocalUtils.getKryo().register(TestPojo.class, 0);
        Serialization serialization = new KryoSerialization();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        TestPojo pojo = new TestPojo("hello", new Date());
        objectOutput.writeObject(pojo);
        objectOutput.flushBuffer();

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serialization.deserialize(inputStream);
        System.out.println(objectInput.readObject(TestPojo.class));
    }

}