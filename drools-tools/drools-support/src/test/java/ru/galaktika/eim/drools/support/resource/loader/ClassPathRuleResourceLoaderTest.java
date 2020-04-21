package ru.galaktika.eim.drools.support.resource.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.api.io.Resource;

import java.io.Reader;

/**
 * @author Petr Titov
 */
public class ClassPathRuleResourceLoaderTest {

	private final static String PATH = "rules/rules.drl";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final ClassPathRuleResourceLoader loader = new ClassPathRuleResourceLoader(null);

	@Test
	public void test() {
		checkResource(loader.load("classpath:" + PATH));
		checkResource(loader.load("classpath://" + PATH));
	}

	@Test
	public void testFail_StringWithoutPrefix() {
		thrown.expect(RuleResourceLoaderException.class);
		loader.load(PATH);
	}

	@Test
	public void testFail_1() {
		thrown.expect(RuleResourceLoaderException.class);
		checkResource(loader.load("classpath:/" + PATH));
	}

	@Test
	public void testFail_2() {
		thrown.expect(RuleResourceLoaderException.class);
		checkResource(loader.load("classpath:///" + PATH));
	}

	private void checkResource(Resource resource) {
		try (Reader reader = resource.getReader()) {
		} catch (Exception e) {
			throw new RuleResourceLoaderException(e.getMessage(), e);
		}
	}
}