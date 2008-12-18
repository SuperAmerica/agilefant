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
    }

    protected PageContext getPageContext() {
        return this.pageContext;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
