package fi.hut.soberit.agilefant.web;

import java.util.Map;

import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * This actions acts as a dispatcher for different context based views. Basic
 * idea of context is that once some context is activated, it can be accessed
 * again by simply executing this action. Caller of action doesn't need to be
 * aware of the current context.
 * 
 * Context also supports some "context object". Basically this is a stored id
 * which will be passed as a parameter to resulting action.
 * 
 * Context view uses following rules: 1) If context is found, result name is
 * "success_<contextName>" 2) If context is not found, result is "success" (aka
 * default view) 3) If context name is passed, current sontext is changed to
 * that context and value is stored to session. 4) If context object id is
 * passed, its value is stored to session. 5) If context is changed (new context !=
 * current context) old object id is removed
 * 
 * All parameters can also be accessed via session using constants defined in
 * this class.
 */

public class ContextViewAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 1992243588151483793L;

    public static final String CONTEXT_NAME = "contextName";

    public static final String CONTEXT_OBJECT_ID = "contextObjectId";

    private String contextName;

    private int contextObjectId;

    @SuppressWarnings("unchecked")
    private Map session;

    @SuppressWarnings("unchecked")
    public String execute() {
        String existingContext = (String) session.get(CONTEXT_NAME);
        if (contextName != null && !contextName.equals(existingContext)) {
            session.remove(CONTEXT_OBJECT_ID);
        }
        if (contextName != null) {
            session.put(CONTEXT_NAME, contextName);
        }
        if (contextObjectId > 0) {
            session.put(CONTEXT_OBJECT_ID, contextObjectId);
        }

        if (session.get(CONTEXT_OBJECT_ID) != null) {
            contextObjectId = (Integer) session.get(CONTEXT_OBJECT_ID);
        }

        String currentContext = (String) session.get(CONTEXT_NAME);

        return (currentContext == null) ? Action.SUCCESS : Action.SUCCESS + "_"
                + currentContext;
    }

    @SuppressWarnings("unchecked")
    public void setSession(Map session) {
        this.session = session;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public int getContextObjectId() {
        return contextObjectId;
    }

    public void setContextObjectId(int contextObjectId) {
        this.contextObjectId = contextObjectId;
    }
}
