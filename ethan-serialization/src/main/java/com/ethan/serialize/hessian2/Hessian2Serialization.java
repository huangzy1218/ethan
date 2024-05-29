package com.ethan.serialize.hessian2;

import com.ethan.serialize.api.ObjectInput;
import com.ethan.serialize.api.ObjectOutput;
import com.ethan.serialize.api.Serialization;

import java.io.*;

/**
 * Hessian2 serialization implementation, Hessian2 is ethan's default serialization protocol.
 *
 * @author Huang Z.Y.
 */
public class Hessian2Serialization implements Serialization, Serializable {

    @Serial
    private static final long serialVersionUID = 2691386627L;

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new Hessian2ObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new Hessian2ObjectInput(input);
    }

}
