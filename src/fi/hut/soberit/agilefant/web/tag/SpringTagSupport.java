package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class SpringTagSupport extends TagSupport {

    private static final long serialVersionUID = -2277915266490971933L;

    private PageContext pageContext;

    private ApplicationContext applicationContext;

    @Override
    public final void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        this.pageContext = pageContext;
        this.applicationContext = WebApplicationContextUtils
                .getWebApplicationContext(pageContext.getServletContext());
        retrieveSingletons();
    }

    /**
     * Stub method that is called after the ApplicationContext has been retrieved.
     */
    protected void retrieveSingletons() {
    }
    
    protected PageContext getPageContext() {
        return this.pageContext;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Retrieves a bean from the ApplicationContext and also does a null check.
     * 
     * @param <T> bean type
     * @param name bean name
     * @return bean that was found
     */
    @SuppressWarnings("unchecked")
    protected <T> T requireBean(String name) {
        Object bean = applicationContext.getBean(name);
        if (bean == null) {
            throw new IllegalStateException("Could not find required bean '" + name + "'");
        }
        return (T) bean;
    }

}
