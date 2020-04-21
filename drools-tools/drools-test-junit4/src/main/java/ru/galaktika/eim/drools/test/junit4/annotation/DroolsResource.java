package ru.galaktika.eim.drools.test.junit4.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * @author Peter Titov
 */
@Target({ TYPE, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DroolsResource {

    /**
     * Path to the resource 
     */
    String value();

    /**
     * Type of the path to the resource
     * 
     *  @see {@link DroolsResourcePathType}
     */
    DroolsResourcePathType pathType() default DroolsResourcePathType.CLASSPATH;

    /**
     * Type of the resource
     * 
     * @see {@link DroolsResourceType}
     */
    DroolsResourceType type() default DroolsResourceType.DRL;
}