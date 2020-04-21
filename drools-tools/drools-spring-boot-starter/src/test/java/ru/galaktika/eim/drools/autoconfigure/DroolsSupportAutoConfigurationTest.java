package ru.galaktika.eim.drools.autoconfigure;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import ru.galaktika.eim.drools.support.DroolsTemplate;

/**
 * @author Petr Titov
 */
public class DroolsSupportAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(DroolsSupportAutoConfiguration.class));

	@Test
	public void testEnabled() {
		contextRunner
			.run(context -> {
				assertThat(context)
					.hasSingleBean(DroolsTemplate.class);
			});
	}

	@Test
	public void testDisabled() {
		contextRunner
			.withPropertyValues(DroolsSupportProperties.PREFIX + ".enabled=false")
			.run(context -> {
				assertThat(context)
					.doesNotHaveBean(DroolsTemplate.class);
			});
	}
}