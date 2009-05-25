package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.ProjectTypeBusiness;

public class ExistingObjectsTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729111L;

    public static final String HAS_PRODUCTS = "hasProducts";

    public static final String HAS_PROJECTS = "hasProjects";

    public static final String HAS_ITERATIONS = "hasIterations";

    public static final String HAS_PROJECTTYPES = "hasProjectTypes";

    private ProductBusiness productBusiness;

    private ProjectBusiness projectBusiness;

    private IterationBusiness iterationBusiness;

    private ProjectTypeBusiness projectTypeBusiness;

    @Override
    public int doStartTag() throws JspException {
        productBusiness = (ProductBusiness) super.getApplicationContext().getBean(
                "productBusiness");
        projectBusiness = (ProjectBusiness) super.getApplicationContext().getBean(
                "projectBusiness");
        iterationBusiness = (IterationBusiness) super.getApplicationContext().getBean(
                "iterationBusiness");
        projectTypeBusiness = (ProjectTypeBusiness) super
                .getApplicationContext().getBean("projectTypeBusiness");
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_PRODUCTS,
                productBusiness.count() > 0);
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_PROJECTS,
                projectBusiness.count() > 0);
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_ITERATIONS,
                iterationBusiness.count() > 0);
        super.getPageContext().setAttribute(
                ExistingObjectsTag.HAS_PROJECTTYPES,
                projectTypeBusiness.count() > 0);
        return Tag.EVAL_BODY_INCLUDE;
    }

}
