package com.tcsl.zodiac.server.core.exception;

/**
 * @className: JraftException
 * @description:
 * @author: yang pan
 */
public class JraftException extends Exception{

    public JraftException(String message) {
        super(message);
    }

    public JraftException(String message, Throwable cause) {
        super(message, cause);
    }
}
