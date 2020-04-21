package ru.galaktika.eim.drools.test.junit4.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class IncompatibleAnnotationsException extends DroolsTestException {

    public IncompatibleAnnotationsException(String message) {
        super(message);
    }
}