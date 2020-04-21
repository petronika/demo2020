package ru.galaktika.eim.drools.support.resource.loader;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.api.io.Resource;

/**
 * @author Petr Titov
 */
public class UriRuleResourceLoaderTest {

	private final static String FILE_PATH = "src/test/resources/rules/rules.drl";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final UriRuleResourceLoader loader = new UriRuleResourceLoader(null);

	@Test
	public void testString() {
		checkResource(loader.load("file:" + FILE_PATH));
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