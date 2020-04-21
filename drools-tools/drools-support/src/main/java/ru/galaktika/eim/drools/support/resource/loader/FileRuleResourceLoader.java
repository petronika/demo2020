package ru.galaktika.eim.drools.support.resource.loader;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import javax.annotation.Nullable;

import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;

/**
 * Supports {@link File}, {@link Path} and {@link String}
 * with prefixes {@link FileRuleResourceLoader#PREFIXES}
 * 
 * @author Petr Titov
 */
public class FileRuleResourceLoader extends AbstractRuleResourceLoader {

	private static final List<String> PREFIXES
			= unmodifiableList(asList("file://", "file:"));

	public FileRuleResourceLoader(@Nullable RuleResourceLoader fallbackLoader) {
		super(fallbackLoader);
	}

	@Override
	protected Optional<Resource> loadInternal(Object source) {
		File file = null;
		if (source instanceof File) {
			file = (File) source;
		} else if (source instanceof Path) {
			file = ((Path) source).toFile();
		}
		if (file != null) {
			return Optional.of(ResourceFactory.newFileResource(file));
		}
		return Optional.of(source)
			.filter(it -> it instanceof String)
    		.map(String.class::cast)
    		.flatMap(this::handleProtocol)
            .map(ResourceFactory::newFileResource);
	}

    private Optional<String> handleProtocol(String source) {
        return PREFIXES.stream()
        		.filter(source::startsWith)
                .map(prefix -> source.substring(prefix.length()))
                .findFirst();
    }
}