package fi.hut.soberit.agilefant.web.tag;

import java.util.ArrayList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.web.page.PageItem;

public class BreadCrumbTrailTag extends SpringTagSupport {

    private static final long serialVersionUID = -8291423940208835187L;

    public static final String PAGE_HIERARCHY = "pageHierarchy";

    private PageItem page = null;

    private ArrayList<PageItem> hierarchy = new ArrayList<PageItem>();

    // private Collection<PageItem> hierarchy = new Stack<PageItem>();

    @Override
    public int doStartTag() throws JspException {
        // if (this.page != null && (hierarchy == null || hierarchy.size() ==
        // 0)) {
        if (this.page != null) {
            this.hierarchy.clear();
            this.hierarchy.add(this.page);
            traverse(this.page);
            super.getPageContext().setAttribute(
                    BreadCrumbTrailTag.PAGE_HIERARCHY, this.hierarchy);
        }
        return Tag.EVAL_BODY_INCLUDE;
    }

    private void traverse(PageItem pi) {
        PageItem parent = pi.getParent();
        if (parent != null) {
            this.hierarchy.add(0, parent);
            traverse(parent);
        }
    }

    public void setProductId(int id) {
        ProductDAO dao = (ProductDAO) super.getApplicationContext().getBean(
                "productDAO");
        this.page = dao.get(id);
    }

    public void setProjectId(int id) {
        ProjectDAO dao = (ProjectDAO) super.getApplicationContext()
                .getBean("projectDAO");
        this.page = dao.get(id);
    }

    public void setIterationId(int id) {
        IterationDAO dao = (IterationDAO) super.getApplicationContext()
                .getBean("iterationDAO");
        this.page = dao.get(id);
    }

    public void setIterationGoalId(int id) {
        IterationGoalDAO dao = (IterationGoalDAO) super.getApplicationContext()
                .getBean("iterationGoalDAO");
        this.page = dao.get(id);
    }

    public void setBacklogItemId(int id) {
        BacklogItemDAO dao = (BacklogItemDAO) super.getApplicationContext()
                .getBean("backlogItemDAO");
        this.page = dao.get(id);
    }

    public void setBacklogId(int id) {
        BacklogDAO dao = (BacklogDAO) super.getApplicationContext().getBean(
                "backlogDAO");
        this.page = (PageItem) dao.get(id);
    }

    public void setTaskId(int id) {
        TaskDAO dao = (TaskDAO) super.getApplicationContext()
                .getBean("taskDAO");
        this.page = dao.get(id);
    }

    public void setUserId(int id) {
        this.page = new User();
    }

}
