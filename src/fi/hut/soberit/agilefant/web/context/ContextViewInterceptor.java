package fi.hut.soberit.agilefant.web.context;

import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import fi.hut.soberit.agilefant.web.ContextAware;

@Component
public class ContextViewInterceptor implements Interceptor {

    private static final long serialVersionUID = 2542730509473988399L;


    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if (invocation.getAction() instanceof ContextAware) {
            ContextAware action = ((ContextAware)invocation.getAction());
            setContext(invocation, action.getContextName(), action.getContextObjectId());
        }

        return invocation.invoke();
    }
    
    private void setContext(ActionInvocation invocation, String contextName, Integer contextObjectId) {
        invocation.getInvocationContext().getSession().put(ContextAware.CONTEXT_OBJECT_ID_AFFIX + contextName, contextObjectId);
        invocation.getInvocationContext().getSession().put(ContextAware.CONTEXT_NAME, contextName);
    }
}
