package com.ethan.serialize.json;

import com.ethan.example.TestPojo;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author Huang Z.Y.
 */
public class JsonSerializationTest {

    @Test
    public void test() {
        JsonSerialization serialization = new JsonSerialization();
        TestPojo pojo = new TestPojo("Hello", new Date());

        String serialize = serialization.serialize(pojo);
        System.out.println(serialize);

    }

}