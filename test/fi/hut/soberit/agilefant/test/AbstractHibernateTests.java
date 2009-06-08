package fi.hut.soberit.agilefant.test;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.junit38.AbstractTransactionalJUnit38SpringContextTests;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

public abstract class AbstractHibernateTests extends
        AbstractTransactionalJUnit38SpringContextTests {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected SimpleJdbcTemplate simpleJdbcTemplate;

    @Autowired
    protected SessionFactory sessionFactory;

    protected void forceFlush() {
        sessionFactory.getCurrentSession().flush();
    }

    protected void executeClassSql() {
        final StackTraceElement callerFromStackTrace = new Throwable()
                .getStackTrace()[1];
        StringBuilder builder = new StringBuilder();
        builder.append("classpath:");
        builder.append(callerFromStackTrace.getClassName().replace('.', '/'));
        builder.append("-data.sql");
        executeSql(builder.toString());
    }

    protected void executeMethodSql() {
        final StackTraceElement callerFromStackTrace = new Throwable()
                .getStackTrace()[1];
        StringBuilder builder = new StringBuilder();
        builder.append("classpath:");
        builder.append(callerFromStackTrace.getClassName().replace('.', '/'));
        builder.append('-');
        builder.append(callerFromStackTrace.getMethodName());
        builder.append("-data.sql");
        executeSql(builder.toString());
    }

    protected void executeSql(String location) {
        Resource resource = applicationContext.getResource(location);
        SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, resource,
                false);
    }

}
