package ru.galaktika.eim.drools.test.junit4.exception;

/**
 * @author Peter Titov
 */
@SuppressWarnings("serial")
public class KnowledgeBuilderException extends DroolsTestException {

    public KnowledgeBuilderException(String message) {
        super(message);
    }
}