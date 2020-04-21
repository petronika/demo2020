package ru.galaktika.eim.drools.autoconfigure;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Petr Titov
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface AvailableForRules {

}