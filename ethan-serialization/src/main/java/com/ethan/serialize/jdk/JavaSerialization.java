package com.ethan.serialize.jdk;

import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java serialization implementation.<br/>
 * <pre>
 *  e.g. &lt;ethan:protocol serialization="java" /&gt;
 * </pre>
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class JavaSerialization implements Serialization {

    private static final AtomicBoolean warn = new AtomicBoolean(false);

    @Override
    public byte getContentTypeId() {
        return 3;
    }

    @Override
    public ObjectOutput serialize(OutputStream out) throws IOException {
        // Perform init that only need to be performed once in a multi-threaded environment
        if (warn.compareAndSet(false, true)) {
            log.error("Java serialization is unsafe.");
        }
        return new JavaObjectOutput(out);
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException {
        if (warn.compareAndSet(false, true)) {
            log.error("Java deserialization is unsafe.");
        }
        return new JavaObjectInput(is);
    }

}
