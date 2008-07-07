package fi.hut.soberit.agilefant.web.context;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class ContextViewInterceptor implements Interceptor {

    private static final long serialVersionUID = 2542730509473988399L;

    public static final String CONTEXT_VIEW_NAME_PARAM = "contextViewName";

    public static final String CONTEXT_OBJECT_ID_PARAM = "contextObjectId";

    public static final String RESET_CONTEXT_PARAM = "resetContextView";

    private ContextViewManager contextViewManager;

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if ("true".equals(this.getRequestParameter(invocation,
                RESET_CONTEXT_PARAM))) {
            contextViewManager.resetContext();
        }

        int objectId = 0;
        String viewName = this.getRequestParameter(invocation,
                CONTEXT_VIEW_NAME_PARAM);
        try {
            objectId = Integer.parseInt(this.getRequestParameter(invocation,
                    CONTEXT_OBJECT_ID_PARAM));
        } catch (NumberFormatException e) {
        }

        if (viewName != null) {
            contextViewManager.setParentContext(viewName, objectId);
        }
        return invocation.invoke();
    }

    private String getRequestParameter(ActionInvocation invocation, String name) {
        Object value = invocation.getInvocationContext().getParameters().get(
                name);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof String[] && ((String[]) value).length > 0) {
            return ((String[]) value)[0];
        }
        return null;
    }

    public void setContextViewManager(ContextViewManager contextViewManager) {
        this.contextViewManager = contextViewManager;
    }
}
