package com.pm.fts.exception;

public class FtsEsException extends Exception{
    private static final long serialVersionUID = 1L;

    public FtsEsException() {
        super();
    }

    public FtsEsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FtsEsException(String message) {
        super(message);
    }

    public FtsEsException(Throwable cause) {
        super(cause);
    }
}
