package com.pm.fts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedEsQueryException extends FtsEsException{
    private static final long serialVersionUID = 1L;

    public UnsupportedEsQueryException() {
        super();
    }

    public UnsupportedEsQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedEsQueryException(String message) {
        super(message);
    }

    public UnsupportedEsQueryException(Throwable cause) {
        super(cause);
    }
}
