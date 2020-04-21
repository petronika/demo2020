package ru.galaktika.eim.drools.test.junit4.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class SessionNotFoundException extends DroolsTestException {

    public SessionNotFoundException(String message) {
        super(message);
    }
}