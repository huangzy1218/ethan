package com.ethan.rpc;

import java.io.Serializable;

/**
 * Invocation (API, Prototype, NonThreadSafe).
 *
 * @author Huang Z.Y.
 * @see Invoker#invoke(Invocation)
 */
public interface Invocation extends Serializable {

    /**
     * Get method name.
     *
     * @return Method name
     */
    String getMethodName();

    /**
     * Get service name.
     *
     * @return Service name
     */
    String getServiceName();

    /**
     * Get parameter type array.
     *
     * @return Parameter type array
     */
    Class<?>[] getParameterTypes();


    /**
     * Get parameter array.
     *
     * @return Parameter array
     */
    Object[] getParameters();

    Object put(Object key, Object value);


    Object get(Object key);

    /**
     * Get attachment by key.
     *
     * @return Attachment value.
     * @serial
     */
    String getAttachment(String key);

    /**
     * For supporting Object transmission.
     */
    void setAttachment(String key, String value);

    /**
     * Get arguments.
     *
     * @return Arguments
     */
    Object[] getArguments();

}
