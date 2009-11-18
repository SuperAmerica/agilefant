package fi.hut.soberit.agilefant.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a real bean that is intended to be tested.
 * 
 * Adds a default Spring bean definition of the field's type into
 * the test context and automatically autowires the instantiated
 * bean so it can be used in tests.
 * 
 * @author Joonas Javanainen
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestedBean {

}
