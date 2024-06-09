package com.ethan.serialize.kryo;

import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Java serialization implementation.<br/>
 * A no-argument constructor must be provided.<br/>
 * <pre>
 *  e.g. &lt;ethan:protocol serialization="kryo" /&gt;
 * </pre>
 *
 * @author Huang Z.Y.
 */
public class KryoSerialization implements Serialization {

    private static final long serialVersionUID = 3033787999037024736L;

    @Override
    public byte getContentTypeId() {
        return 4;
    }

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new KryoObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new KryoObjectInput(input);
    }

}
