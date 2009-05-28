package fi.hut.soberit.agilefant.web.tag;

import java.util.HashMap;
import java.util.Map;

import fi.hut.soberit.agilefant.business.TodoBusiness;
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

        
        Map<String, Integer> map = new HashMap<String, Integer>();
        TodoBusiness todoBusiness = requireBean("todoBusiness");
        Map<Integer,Integer> tmp = todoBusiness.getTodoCountByState(backlogItemId);

        map.put(NOT_STARTED, tmp.get(State.NOT_STARTED.getOrdinal()));
        map.put(STARTED, tmp.get(State.STARTED.getOrdinal()));
        map.put(PENDING, tmp.get(State.PENDING.getOrdinal()));
        map.put(BLOCKED, tmp.get(State.BLOCKED.getOrdinal()));
        map.put(IMPLEMENTED, tmp.get(State.IMPLEMENTED.getOrdinal()));
        map.put(DONE, tmp.get(State.DONE.getOrdinal()));

        super.getPageContext().setAttribute(super.getId(), map);
        return EVAL_PAGE;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

}
