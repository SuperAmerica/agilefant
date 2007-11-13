package fi.hut.soberit.agilefant.web.context;

import java.util.EmptyStackException;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.opensymphony.xwork.ActionContext;

public class DefaultContextViewManager implements ContextViewManager {

    private static final Logger log = Logger
            .getLogger(DefaultContextViewManager.class);

    public static final String DEFAULT_CONTEXT_STACK_PARAM = "contextViewStack";

    private String contextStackParam = DEFAULT_CONTEXT_STACK_PARAM;

    public ContextView getParentContext() {
        try {
            return this.getStack().pop();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    public void setParentContext(ContextView context) {
        if (context != null) {
            this.getStack().push(context);
            log.debug("Parent context set to: name=" + context.getContextName()
                    + ", object=" + context.getContextObject());
        }
    }

    public void setParentContext(String contextName, int objectId) {
        ContextView top = this.getParentContext();
        ContextView newTop = null;
        if (top == null || !top.getContextName().equals(contextName)
                || top.getContextObject() != objectId) {
            newTop = new ContextView(contextName, objectId);
        }
        if (top != null) {
            this.setParentContext(top);
        }
        if (newTop != null) {
            this.setParentContext(newTop);
        }
    }

    public void resetContext() {
        this.getStack().clear();
    }

    private Stack<ContextView> getStack() {
        Stack<ContextView> stack = (Stack<ContextView>) ActionContext
                .getContext().getSession().get(contextStackParam);
        if (stack == null) {
            stack = new Stack<ContextView>();
            ActionContext.getContext().getSession().put(contextStackParam,
                    stack);
        }
        return stack;
    }

    public void setContextStackParam(String contextStackParam) {
        this.contextStackParam = contextStackParam;
    }

}
