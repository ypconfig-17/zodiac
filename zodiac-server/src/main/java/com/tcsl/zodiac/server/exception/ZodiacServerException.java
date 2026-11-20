package com.tcsl.zodiac.server.exception;

/**
 * @className: ZodiacServerException
 * @description:
 * @author: yang pan
 */
public class ZodiacServerException extends RuntimeException{


    private static final long serialVersionUID = -4055094214717871912L;

    public ZodiacServerException() {
    }

    public ZodiacServerException(String message) {
        super(message);
    }

    public ZodiacServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZodiacServerException(Throwable cause) {
        super(cause);
    }

    public ZodiacServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

