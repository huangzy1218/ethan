package com.ethan.rpc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * Simply represents the business result.
 *
 * @author Huang Z.Y.
 */
@NoArgsConstructor
@AllArgsConstructor
public class AppResponse implements Result {

    @Serial
    private static final long serialVersionUID = -6925924956850004727L;

    private Object result;

    @Setter
    @Getter
    private String requestId;

    private Throwable exception;

    public AppResponse(Object result) {
        this.result = result;
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

}
