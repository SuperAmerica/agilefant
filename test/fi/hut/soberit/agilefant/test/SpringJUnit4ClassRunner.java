package fi.hut.soberit.agilefant.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

/**
 * A custom version of Spring's SpringJUnit4ClassRunner that supports JUnit 4.6 properly.
 * 
 * The standard SpringJUnit4ClassRunner extends JUnit internal classes that have been removed
 * as of version 4.6, so it cannot be used at all.
 * This class is basically a rewrite of the standard class using proper JUnit 4.6 runner
 * classes.
 * 
 * @author jtjavana
 *
 */
public class SpringJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private final TestContextManager testContextManager;

    public SpringJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
        testContextManager = new TestContextManager(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        testContextManager.prepareTestInstance(testInstance);
        return testInstance;
    }

    @Override
    protected Statement withBefores(final FrameworkMethod method,
            final Object target, Statement statement) {
        final Statement original = super.withBefores(method, target, statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                testContextManager.beforeTestMethod(target, method.getMethod());
                original.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfters(final FrameworkMethod method,
            final Object target, final Statement statement) {
        final Statement original = super.withAfters(method, target, statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable exception = null;
                try {
                    statement.evaluate();
                } catch (Throwable throwable) {
                    exception = throwable;
                }
                testContextManager.afterTestMethod(target, method.getMethod(),
                        exception);
                original.evaluate();
            }
        };
    }

}
