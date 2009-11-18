package fi.hut.soberit.agilefant.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.transaction.NotSupportedException;

import org.springframework.context.annotation.Scope;

public final class SpringAssertions {

    public static void assertNoScopeAnnotation(Class<?> clazz) {
        Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
        assertNull(scopeAnnotation);
    }

    public static void assertScopeAnnotation(String scope, Class<?> clazz) {
        Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
        assertNotNull(scopeAnnotation);
        assertEquals(scope, scopeAnnotation.value());
    }

    private SpringAssertions() throws NotSupportedException {
        throw new NotSupportedException("Utility class constructor called");
    }

}
