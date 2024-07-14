package com.ethan.rpc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Simply represents the business result.
 *
 * @author Huang Z.Y.
 */
@NoArgsConstructor
@AllArgsConstructor
public class AppResponse implements Result {

    private static final long serialVersionUID = 4200477403478011425L;
    private Object result;
    @Setter
    @Getter
    private String requestId;
    private Throwable exception;
    /**
     * Used to receive future result.
     */
    private Invocation invocation;


    public AppResponse(Object result) {
        this.result = result;
    }

    public AppResponse(Invocation invocation) {
        this.invocation = invocation;
    }

    public AppResponse(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public Object getValue() {
        return result;
    }

    @Override
    public void setValue(Object value) {
        this.result = value;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void setException(Throwable t) {
        this.exception = t;
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException(
                "AppResponse represents an concrete business response, there will be no status changes, " +
                        "you should get internal values directly.");
    }

}
