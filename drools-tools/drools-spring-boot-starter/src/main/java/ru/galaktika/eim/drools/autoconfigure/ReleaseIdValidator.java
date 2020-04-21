package ru.galaktika.eim.drools.autoconfigure;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Petr Titov
 */
public class ReleaseIdValidator implements ConstraintValidator<ReleaseIdConstraint, ReleaseId> {

	@Override
	public boolean isValid(ReleaseId value, ConstraintValidatorContext context) {
		boolean result = value == null
				//|| (value.getGroupId() == null && value.getArtifactId() == null && value.getVersion() == null)
				|| (value.getGroupId() != null && value.getArtifactId() != null && value.getVersion() != null);
		if (!result) {
			String message = "All properties of ReleaseId must have a value";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
		}
		return true;
	}
}