package fi.hut.soberit.agilefant.web.tag;

import java.util.HashMap;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;

public class StateListTag extends SpringTagSupport {

    private static final String NOT_STARTED = "notStarted";

    private static final String STARTED = "started";
    
    private static final String PENDING = "pending";

    private static final String BLOCKED = "blocked";
    
    private static final String IMPLEMENTED = "implemented";

    private static final String DONE = "done";

    private static final long serialVersionUID = 2586151152192294611L;

    private int backlogItemId;

    @Override
    public int doEndTag() throws javax.servlet.jsp.JspTagException {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        TaskDAO dao = (TaskDAO) super.getApplicationContext()
                .getBean("taskDAO");
        BacklogItemDAO bliDao = (BacklogItemDAO) super.getApplicationContext()
                .getBean("backlogItemDAO");
        BacklogItem bli = bliDao.get(backlogItemId);

        int notStarted = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.NOT_STARTED }).size();
        int started = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.STARTED }).size();
        int pending = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.PENDING }).size();
        int blocked = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.BLOCKED }).size();
        int implemented = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.IMPLEMENTED }).size();
        int done = dao.getTasksByStateAndBacklogItem(bli,
                new State[] { State.DONE }).size();
        
        map.put(NOT_STARTED, notStarted);
        map.put(STARTED, started);
        map.put(PENDING, pending);
        map.put(BLOCKED, blocked);
        map.put(IMPLEMENTED, implemented);
        map.put(DONE, done);
        
        super.getPageContext().setAttribute(super.getId(), map);
        return EVAL_PAGE;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

}
