package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;

public class ExistingObjectsTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729111L;

    public static final String HAS_PRODUCTS = "hasProducts";

    public static final String HAS_PROJECTS = "hasProjects";

    public static final String HAS_ITERATIONS = "hasIterations";

    private ProductBusiness productBusiness;

    private ProjectBusiness projectBusiness;

    private IterationBusiness iterationBusiness;

    @Override
    public int doStartTag() throws JspException {
        productBusiness = requireBean("productBusiness");
        projectBusiness = requireBean("projectBusiness");
        iterationBusiness = requireBean("iterationBusiness");
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_PRODUCTS,
                productBusiness.countAll() > 0);
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_PROJECTS,
                projectBusiness.countAll() > 0);
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_ITERATIONS,
                iterationBusiness.countAll() > 0);
        return Tag.EVAL_BODY_INCLUDE;
    }

}
