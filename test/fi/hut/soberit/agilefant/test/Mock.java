package fi.hut.soberit.agilefant.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a mock.
 * 
 * The field will be mocked with EasyMock.createMock
 * and automatically autowired so it can be used in tests
 * 
 * @author Joonas Javanainen
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mock {
}
