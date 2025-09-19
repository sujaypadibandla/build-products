package com.gymplus.core;

/** application specific runtime exception, used instead of throwing generic RuntimeException (plus providing some helper formatting methods) */
public class GspRuntimeException extends RuntimeException {

    public GspRuntimeException() {
        super();
    }

    public GspRuntimeException(Throwable cause) {
        super(cause);
    }
    
    public GspRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GspRuntimeException(String format, Object... args) {
        super(String.format(format, args));
    }

    public GspRuntimeException(Throwable cause, String format, Object... args) {
        this(String.format(format, args), cause);
    }

    private static final long serialVersionUID = -6467942208760351415L;
}
