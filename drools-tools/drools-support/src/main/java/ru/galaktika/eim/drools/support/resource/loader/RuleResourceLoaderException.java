package ru.galaktika.eim.drools.support.resource.loader;

/**
 * @author Petr Titov
 */
@SuppressWarnings("serial")
public class RuleResourceLoaderException extends RuntimeException {

	public RuleResourceLoaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleResourceLoaderException(String message) {
		super(message);
	}
}