package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspTagException;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;

public class PercentDoneTag extends SpringTagSupport {

    private static final long serialVersionUID = 2586151152192294611L;

    private int backlogItemId;

    @Override
    public int doEndTag() throws javax.servlet.jsp.JspTagException {

        TaskDAO dao = requireBean("taskDAO");
        BacklogItemDAO bliDao = requireBean("backlogItemDAO");
        BacklogItem bli = bliDao.get(backlogItemId);

        int done = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.DONE }).size();

        // TODO: Use HQL-query instead of arithmetics here to calculate #
        int total = bli.getTasks().size();
        int percentDone = 100;
        if (total > 0) {
            percentDone = Math.round(done * 100 / total);
        }

        try {
            super.getPageContext().getOut().write(String.valueOf(percentDone));
        } catch (java.io.IOException e) {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

}
