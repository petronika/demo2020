package ru.galaktika.eim.drools.test.junit4.annotation;

/**
 * @author Peter Titov
 */
public enum DroolsResourcePathType {

    /** @see {@link org.kie.internal.io.ResourceFactory#newClassPathResource(String)} */
    CLASSPATH,

    /** @see {@link org.kie.internal.io.ResourceFactory#newFileResource(String)} */
    FILE,

    /** @see {@link org.kie.internal.io.ResourceFactory#newUrlResource(String)} */
    URL;
}