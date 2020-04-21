package ru.galaktika.eim.drools.test.junit4.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class ResourceException extends DroolsTestException {

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}