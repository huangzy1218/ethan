package com.ethan.remoting.tansport;

import com.ethan.rpc.CodecSupport;
import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import example.TestPojo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * @author Huang Z.Y.
 */
class CodecSupportTest {

    @Test
    public void testCodecSupportSerialization() throws Exception {
        Serialization serialization = CodecSupport.getSerialization((byte) 1);
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
    public void testCodecSupportCompressor() throws Exception {
        Compressor compressor = CodecSupport.getCompressor((byte) 1);
        byte[] bytes = compressor.compress("Hello World".getBytes());
        byte[] decompressBytes = compressor.decompress(bytes);
        Assertions.assertEquals("Hello World", new String(decompressBytes));
    }

}