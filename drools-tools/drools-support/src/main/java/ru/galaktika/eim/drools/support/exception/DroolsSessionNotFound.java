package ru.galaktika.eim.drools.support.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class DroolsSessionNotFound extends DroolsException {

    public DroolsSessionNotFound(String sessionName) {
        super(String.format("The session with name '%s' not found", sessionName));
    }
}