package com.ethan.serialize.api;

import java.io.IOException;

/**
 * Object output interface.
 *
 * @author Huang Z.Y.
 */
public interface ObjectOutput extends DataOutput {

    /**
     * Write object.
     *
     * @param obj Object
     */
    void writeObject(Object obj) throws IOException;

}
