package ru.galaktika.eim.drools.support.resource.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.Optional;

import javax.annotation.Nullable;

import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;

/**
 * Supports {@link URI}, {@link URL} and {@link String}
 * 
 * @author Petr Titov
 */
public class UriRuleResourceLoader extends AbstractRuleResourceLoader {

	public UriRuleResourceLoader(@Nullable RuleResourceLoader fallbackLoader) {
		super(fallbackLoader);
	}

	@Override
	protected Optional<Resource> loadInternal(Object source) {
		URL url = null;
		try {
			if (source instanceof URI) {
				url = ((URI) source).toURL();
			} else if (source instanceof URL) {
				url = (URL) source;
			} else if (source instanceof String) {
				url = new URL((String) source);
			}
		} catch (MalformedURLException e) {
			// ignore
		}
		return Optional.ofNullable(url)
				.map(ResourceFactory::newUrlResource);
	}
}