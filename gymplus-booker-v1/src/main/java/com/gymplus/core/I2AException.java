package com.gymplus.core;

/** application specific exception, used instead of throwing generic Exception (plus providing some helper formatting methods) */
public class I2AException extends Exception {

    public int code = -1;

    public I2AException() {
        super();
    }

    public I2AException(Throwable cause, String message) {
        super(message, cause);
    }

    public I2AException(String format, Object... args) {
        super(String.format(format, args));
    }

    public I2AException(int code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
    }

    public I2AException(Throwable cause, String format, Object... args) {
        this(cause, String.format(format, args));
    }

    public I2AException(Throwable cause, int code) {
        this(cause, "");
        this.code = code;
    }

    public I2AException(int code) {
        this();
        this.code = code;
    }

    private static final long serialVersionUID = 1L;
}
