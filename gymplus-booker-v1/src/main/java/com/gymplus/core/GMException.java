package com.gymplus.core;


public class GMException extends RuntimeException {
    
    public GMException(int code, String message, Throwable cause) {
        super(code + " " + message, cause);
        _code = code;
        _message = message;
    }

    public GMException(int code, String message) {
        super(code + " " + message);
        _code = code;
        _message = message;
    }

    @Override
    public String getMessage() {
        return _message;
    }
    
    public void setMessage(String message) {
        _message = message;
    }
    
    @Override
    public String toString() {
        return getMessage();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GMException)) { return false; }
        return ((GMException)obj)._code == _code;
    }

    @Override
    public int hashCode() {
        return ((Integer)_code).hashCode();
    }

    private int _code = 0;
    private String _message = "";
    private Gmap _extra = new Gmap();
    private static final long serialVersionUID = 6892279182519640162L;

}
