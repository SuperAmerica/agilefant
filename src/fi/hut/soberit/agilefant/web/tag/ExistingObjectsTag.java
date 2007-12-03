package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;

public class ExistingObjectsTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729111L;

    public static final String HAS_PRODUCTS = "hasProducts";

    public static final String HAS_PROJECTS = "hasProjects";

    public static final String HAS_ITERATIONS = "hasIterations";

    public static final String HAS_ACTIVITYTYPES = "hasActivityTypes";

    private ProductDAO productDAO;

    private ProjectDAO projectDAO;

    private IterationDAO iterationDAO;

    private ActivityTypeDAO activityTypeDAO;

    @Override
    public int doStartTag() throws JspException {
        productDAO = (ProductDAO) super.getApplicationContext().getBean(
                "productDAO");
        projectDAO = (ProjectDAO) super.getApplicationContext()
                .getBean("projectDAO");
        iterationDAO = (IterationDAO) super.getApplicationContext().getBean(
                "iterationDAO");
        activityTypeDAO = (ActivityTypeDAO) super.getApplicationContext()
                .getBean("activityTypeDAO");
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_PRODUCTS,
                !productDAO.getAll().isEmpty());
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_PROJECTS,
                !projectDAO.getAll().isEmpty());
        super.getPageContext().setAttribute(ExistingObjectsTag.HAS_ITERATIONS,
                !iterationDAO.getAll().isEmpty());
        super.getPageContext().setAttribute(
                ExistingObjectsTag.HAS_ACTIVITYTYPES,
                !activityTypeDAO.getAll().isEmpty());
        return Tag.EVAL_BODY_INCLUDE;
    }
}
