package ru.galaktika.eim.drools.support.resource.loader;

import java.util.List;
import java.util.Optional;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import javax.annotation.Nullable;

import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;

/**
 * Supports {@link String} with prefixes {@link ClassPathRuleResourceLoader#PREFIXES}
 * 
 * @author Petr Titov
 */
public class ClassPathRuleResourceLoader extends AbstractRuleResourceLoader {

	private static final List<String> PREFIXES
			= unmodifiableList(asList("classpath://", "classpath:"));

	public ClassPathRuleResourceLoader(@Nullable RuleResourceLoader fallbackLoader) {
		super(fallbackLoader);
	}

	@Override
	protected Optional<Resource> loadInternal(Object source) {
		return Optional.of(source)
			.filter(it -> it instanceof String)
    		.map(String.class::cast)
    		.flatMap(this::handleProtocol)
            .map(ResourceFactory::newClassPathResource);
	}

    private Optional<String> handleProtocol(String source) {
        return PREFIXES.stream()
        		.filter(source::startsWith)
				.map(prefix -> /* "/" + */ source.substring(prefix.length()))
                .findFirst();
    }
}