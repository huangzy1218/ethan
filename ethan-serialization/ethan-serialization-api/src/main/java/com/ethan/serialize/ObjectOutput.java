package com.ethan.serialize;

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
