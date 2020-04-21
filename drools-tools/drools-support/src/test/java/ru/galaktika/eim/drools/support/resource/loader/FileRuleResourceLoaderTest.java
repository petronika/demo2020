package ru.galaktika.eim.drools.support.resource.loader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.api.io.Resource;

/**
 * @author Petr Titov
 */
public class FileRuleResourceLoaderTest {

	private final static String PATH = "src/test/resources/rules/rules.drl";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final FileRuleResourceLoader loader = new FileRuleResourceLoader(null);

	@Test
	public void testString() {
		checkResource(loader.load("file:" + PATH));
		checkResource(loader.load("file://" + PATH));
	}

	@Test
	public void testFile() {
		checkResource(loader.load(new File(PATH)));
	}

	@Test
	public void testPath() {
		checkResource(loader.load(Paths.get(PATH)));
	}

	@Test
	public void testFail_StringWithoutPrefix() {
		thrown.expect(RuleResourceLoaderException.class);
		loader.load(PATH);
	}

	@Test
	public void testFail_StringMissingFile() {
		thrown.expect(RuleResourceLoaderException.class);
		loader.load("rules.drl");
	}

	private void checkResource(Resource resource) {
		try (Reader reader = resource.getReader()) {
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}