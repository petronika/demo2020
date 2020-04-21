package ru.galaktika.eim.drools.support.resource.loader;

import java.util.Optional;
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import org.kie.api.io.Resource;

/**
 * @author Petr Titov
 */
public abstract class AbstractRuleResourceLoader implements RuleResourceLoader {

	@Nullable
	private final RuleResourceLoader fallbackLoader;

	protected AbstractRuleResourceLoader(@Nullable RuleResourceLoader fallbackLoader) {
		this.fallbackLoader = fallbackLoader;
	}

	@Override
	public Resource load(Object source) throws RuleResourceLoaderException {
		requireNonNull(source);
		try {
			Resource resource = loadInternal(source)
				.orElseGet(() -> ofNullable(fallbackLoader)
					.map(fallbackLoader -> fallbackLoader.load(source))
					.orElseThrow(() -> new RuleResourceLoaderException("Resource not found: " + source)));
			//try (Reader reader = resource.getReader()) {} // check if exists
			return resource;
		} catch (Exception e) {
			if (!(e instanceof RuleResourceLoaderException)) {
				e = new RuleResourceLoaderException(e.getMessage(), e);
			}
			throw (RuleResourceLoaderException) e;
		}
	}

	protected abstract Optional<Resource> loadInternal(Object source);
}