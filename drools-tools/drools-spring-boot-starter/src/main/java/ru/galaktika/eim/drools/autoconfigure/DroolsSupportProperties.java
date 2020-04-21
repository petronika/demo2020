package ru.galaktika.eim.drools.autoconfigure;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * @author Petr Titov
 */
@ConfigurationProperties(DroolsSupportProperties.PREFIX)
@Data
@Validated
public class DroolsSupportProperties {

	public static final String PREFIX = "galaktika.eim.drools.support";

	public static final String DEFAULT_LOGGER_NAME = "drools.support.logger";

	@Nullable
	private List<String> probeSessions;

	@Nullable
	private String loggerName = DEFAULT_LOGGER_NAME;

	@Nullable
	@ReleaseIdConstraint
	private ReleaseId releaseId;
}