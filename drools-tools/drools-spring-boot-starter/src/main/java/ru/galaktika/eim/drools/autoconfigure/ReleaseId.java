package ru.galaktika.eim.drools.autoconfigure;

import lombok.Data;

/**
 * @author Petr Titov
 */
@Data
public class ReleaseId {

	private String groupId;

	private String artifactId;

	private String version;
}