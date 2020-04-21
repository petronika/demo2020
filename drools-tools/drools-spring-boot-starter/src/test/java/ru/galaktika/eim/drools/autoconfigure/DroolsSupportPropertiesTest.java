package ru.galaktika.eim.drools.autoconfigure;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Petr Titov
 */
public class DroolsSupportPropertiesTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void test() {
		DroolsSupportProperties bean = new DroolsSupportProperties();

		Set<ConstraintViolation<DroolsSupportProperties>> constraintViolations;

		constraintViolations = validator.validate(bean);
		assertTrue(constraintViolations.isEmpty());

		bean.setLoggerName("someLogger");
		bean.setProbeSessions(Collections.singletonList("someSession"));

		constraintViolations = validator.validate(bean);
		assertTrue(constraintViolations.isEmpty());
	}

	@Test
	public void testReleaseId() {
		DroolsSupportProperties bean = new DroolsSupportProperties();

		Set<ConstraintViolation<DroolsSupportProperties>> constraintViolations;

		ReleaseId releaseId = new ReleaseId();
		bean.setReleaseId(releaseId);

		constraintViolations = validator.validate(bean);
		assertEquals(1, constraintViolations.size());
		assertEquals("All properties of ReleaseId must have a value", constraintViolations.iterator().next().getMessage());

		releaseId.setGroupId("groupId");

		constraintViolations = validator.validate(bean);
		assertEquals(1, constraintViolations.size());
		assertEquals("All properties of ReleaseId must have a value", constraintViolations.iterator().next().getMessage());

		releaseId.setArtifactId("artifactId");

		constraintViolations = validator.validate(bean);
		assertEquals(1, constraintViolations.size());
		assertEquals("All properties of ReleaseId must have a value", constraintViolations.iterator().next().getMessage());
		
		releaseId.setVersion("version");

		constraintViolations = validator.validate(bean);
		assertTrue(constraintViolations.isEmpty());
	}
}