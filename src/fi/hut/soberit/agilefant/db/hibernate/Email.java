package fi.hut.soberit.agilefant.db.hibernate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.validator.ValidatorClass;

/**
 * Annotation to enable our custom email validator.
 * 
 * @author Turkka Äijälä
 * @see EmailValidator
 */
@Documented
@ValidatorClass(EmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    String message() default "is not a proper email address";
}