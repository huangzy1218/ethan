package com.ethan.serialize.hessian2;

import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Hessian2 serialization implementation, Hessian2 is ethan's default serialization protocol.
 *
 * @author Huang Z.Y.
 */
public class Hessian2Serialization implements Serialization, Serializable {

    private static final long serialVersionUID = 7548538779065353697L;

    @Override
    public byte getContentTypeId() {
        return 2;
    }

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new Hessian2ObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new Hessian2ObjectInput(input);
    }

}
