package ru.galaktika.eim.drools.support.resource.loader;

import java.util.function.Function;

import org.kie.api.io.Resource;

/**
 * @author Petr Titov
 */
@FunctionalInterface
public interface RuleResourceLoader extends Function<Object, Resource> {

	@Override
	default Resource apply(Object source) throws RuleResourceLoaderException {
		return load(source);
	}

	Resource load(Object source);
}