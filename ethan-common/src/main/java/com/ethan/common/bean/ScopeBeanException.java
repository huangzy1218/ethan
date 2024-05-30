package com.ethan.common.bean;

/**
 * Custom exception class for scope-related bean errors.
 *
 * @author Huang Z.Y.
 */
public class ScopeBeanException extends RuntimeException {

    public ScopeBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScopeBeanException(String message) {
        super(message);
    }

}
