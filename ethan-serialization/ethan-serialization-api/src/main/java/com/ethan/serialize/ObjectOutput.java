package com.ethan.serialize;

import java.io.IOException;
import java.util.Map;

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

    default void writeEvent(String data) throws IOException {
        writeObject(data);
    }

    default void writeAttachments(Map<String, Object> attachments) throws IOException {
        writeObject(attachments);
    }

}
