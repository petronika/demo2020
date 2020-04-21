package ru.galaktika.eim.drools.support.resource.loader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;
import org.kie.api.io.Resource;

/**
 * @author Petr Titov
 */
public class RuleResourceLoaderChainTest {

	private final static String CLASS_PATH = "rules/rules.drl";

	private final static String FILE_PATH = "src/test/resources/rules/rules.drl";

	private final RuleResourceLoader loader
		= new FileRuleResourceLoader(
			new ClassPathRuleResourceLoader(
				new UriRuleResourceLoader(null)));

	@Test
	public void testFileString() {
		checkResource(loader.load("file:" + FILE_PATH));
		checkResource(loader.load("file://" + FILE_PATH));
	}

	@Test
	public void testClassPathString() {
		checkResource(loader.load("classpath:" + CLASS_PATH));
		checkResource(loader.load("classpath://" + CLASS_PATH));
	}

	@Test
	public void testFile() {
		checkResource(loader.load(new File(FILE_PATH)));
	}

	@Test
	public void testPath() {
		checkResource(loader.load(Paths.get(FILE_PATH)));
	}

	@Test
	public void testURI() throws Exception {
		checkResource(loader.load(new URI("file:" + FILE_PATH)));
	}

	@Test
	public void testURL() throws Exception {
		checkResource(loader.load(new URL("file:" + FILE_PATH)));
	}

	private void checkResource(Resource resource) {
		try (Reader reader = resource.getReader()) {
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}