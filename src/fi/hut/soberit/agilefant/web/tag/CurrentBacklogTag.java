package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public class CurrentBacklogTag extends SpringTagSupport {

    private static final long serialVersionUID = 828242085743658537L;

    public static final String CURRENT_ACTION = "currentAction";
    public static final String CURRENT_CONTEXT = "currentContext";
    public static final String CURRENT_PAGE_ID = "currentPageId";
    public static final String CURRENT_BACKLOG_CONTEXT = "currentBacklogContext";
    public static final String CURRENT_BACKLOG_ID = "currentBacklogId";
    public static final String CURRENT_PRODUCT_ID = "currentProductId";
    public static final String CURRENT_PROJECT_ID = "currentProjectId";
    public static final String CURRENT_ITERATION_ID = "currentIterationId";

    private String currentAction = "";
    private String currentContext = "";
    private Integer currentPageId = 0;
    private Integer currentProductId = 0;
    private Integer currentProjectId = 0;
    private Integer currentIterationId = 0;

    private BacklogBusiness backlogBusiness;

    private Backlog backlog;

    private int backlogId;

    @Override
    public int doStartTag() throws JspException {
        backlogBusiness = requireBean("backlogBusiness");
        this.backlog = backlogBusiness.retrieveIfExists(backlogId);
        this.resolveIds();
        if (backlog == null) {
            setPageAttribute(CURRENT_BACKLOG_ID, getSessionAttribute(CURRENT_BACKLOG_ID));
            setPageAttribute(CURRENT_BACKLOG_CONTEXT,  getSessionAttribute(CURRENT_BACKLOG_CONTEXT));
        } else {
            setSessionAttribute(CURRENT_BACKLOG_ID, currentPageId);
            setSessionAttribute(CURRENT_BACKLOG_CONTEXT, currentContext);
        }
        super.getPageContext().setAttribute(CurrentBacklogTag.CURRENT_ACTION,
                this.currentAction);
        super.getPageContext().setAttribute(CurrentBacklogTag.CURRENT_CONTEXT,
                this.currentContext);
        super.getPageContext().setAttribute(CurrentBacklogTag.CURRENT_PAGE_ID,
                this.currentPageId);
        super.getPageContext().setAttribute(
                CurrentBacklogTag.CURRENT_PRODUCT_ID, this.currentProductId);
        super.getPageContext().setAttribute(
                CurrentBacklogTag.CURRENT_PROJECT_ID, this.currentProjectId);
        super.getPageContext()
                .setAttribute(CurrentBacklogTag.CURRENT_ITERATION_ID,
                        this.currentIterationId);
        return Tag.EVAL_BODY_INCLUDE;
    }

    private void resolveIds() {
        if (backlog instanceof Product) {
            currentAction = "editProduct";
            currentContext = "product";
            currentPageId = backlog.getId();
            currentProductId = backlog.getId();
            currentProjectId = null;
            currentIterationId = null;
        } else if (backlog instanceof Project) {
            currentAction = "editProject";
            currentContext = "project";
            currentPageId = backlog.getId();
            currentProductId = backlog.getParent().getId();
            currentProjectId = backlog.getId();
            currentIterationId = null;
        } else if (backlog instanceof Iteration) {
            currentAction = "editIteration";
            currentContext = "iteration";
            currentPageId = backlog.getId();
            currentProductId = backlog.getParent().getParent().getId();
            currentProjectId = backlog.getParent().getId();
            currentIterationId = backlog.getId();
        }
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }
}
