package fi.hut.soberit.agilefant.test;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

/**
 * A custom AutowiredAnnotationBeanPostProcessor that ignores the 'required'
 * -attribute in the annotation.
 */
public class MockAutowiredAnnotationBeanPostProcessor extends
        AutowiredAnnotationBeanPostProcessor {

    @Override
    protected boolean determineRequiredStatus(Annotation annotation) {
        return false;
    }

}
