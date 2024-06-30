package com.ethan.rpc;

/**
 * Invocation (API, Prototype, NonThreadSafe).
 *
 * @author Huang Z.Y.
 * @see Invoker#invoke(Invocation)
 */
public interface Invocation {

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


}
