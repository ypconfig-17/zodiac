package com.tcsl.zodiac.server.exception;

/**
 * @className: ResponsityException
 * @description:
 * @author: yang pan
 */
public class ResponsityException extends RuntimeException{


    private static final long serialVersionUID = -4055094214717871912L;

    public ResponsityException() {
    }

    public ResponsityException(String message) {
        super(message);
    }

    public ResponsityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponsityException(Throwable cause) {
        super(cause);
    }

    public ResponsityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

