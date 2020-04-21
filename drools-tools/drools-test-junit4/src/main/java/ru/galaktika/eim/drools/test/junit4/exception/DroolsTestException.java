package ru.galaktika.eim.drools.test.junit4.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class DroolsTestException extends RuntimeException {

    public DroolsTestException(String message) {
        super(message);
    }

    public DroolsTestException(String message, Throwable cause) {
        super(message, cause);
    }
}