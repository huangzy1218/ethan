package com.ethan.rpc;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Invoke result.
 *
 * @author Huang Z.Y.
 */
public interface Result extends Serializable {

    /**
     * Get invoke result.
     *
     * @return Result, if no result return null
     */
    Object getValue();

    void setValue(Object value);

    /**
     * Get exception.
     *
     * @return Exception, if no exception return null
     */
    Throwable getException();

    void setException(Throwable t);

    /**
     * Has exception.
     *
     * @return Has exception
     */
    boolean hasException();


    Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

}
