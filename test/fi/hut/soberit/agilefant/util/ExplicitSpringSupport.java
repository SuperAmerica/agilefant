package fi.hut.soberit.agilefant.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Offers spring support in agilefant context when running outside Tomcat.
 * Implemented for testing, should not be used for code running in the
 * webapplication itself.
 * <p>
 * <b>Usage:</b><br>
 * <br>
 * In constructor:<br>
 * <code> 
 * ExplicitSpringSupport.loadSpringSupport(this);
 * </code> <br>
 * <br>
 * Alternatively, in class body:<br>
 * <code>
 * ExplicitSpringSupport springSupport = new ExplicitSpringSupport(this);
 * </code>
 * 
 * @author Turkka Äijälä
 */
public class ExplicitSpringSupport {
    /**
     * Spring configuration files.
     */
    private static final String[] configLocations = {
            "file:conf/testApplicationContext.xml",
            "file:conf/testApplicationContext-daos.xml",
            "file:conf/applicationContext-actions.xml" };

    /**
     * Singleton instance of the application context. Used to ensure spring is
     * only loaded once.
     */
    private static AbstractApplicationContext agilefantContext = null;

    /**
     * Get the singleton isntance
     * 
     * @return singleton instance
     */
    private static AbstractApplicationContext getAgilefantSpringContext() {
        if (agilefantContext == null) {

            // get the static logger
            Log log = LogFactory.getLog(ExplicitSpringSupport.class);

            // inform that we're initializing spring
            log.info("initializing spring");

            // create the context
            agilefantContext = new ClassPathXmlApplicationContext(
                    configLocations);

            log.info("done initializing spring");
        }

        return agilefantContext;
    }

    public ExplicitSpringSupport(Object target) {
        getAgilefantSpringContext().getAutowireCapableBeanFactory()
                .autowireBeanProperties(target,
                        AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
    }

    public static void loadSpringSupport(Object target) {
        getAgilefantSpringContext().getAutowireCapableBeanFactory()
                .autowireBeanProperties(target,
                        AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
    }
}
