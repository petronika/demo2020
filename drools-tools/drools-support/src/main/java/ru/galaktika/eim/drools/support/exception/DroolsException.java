package ru.galaktika.eim.drools.support.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class DroolsException extends RuntimeException {

    public DroolsException() {
    }

    public DroolsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DroolsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DroolsException(String message) {
        super(message);
    }

    public DroolsException(Throwable cause) {
        super(cause);
    }
}