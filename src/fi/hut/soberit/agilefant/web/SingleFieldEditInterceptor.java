package fi.hut.soberit.agilefant.web;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Interceptor, which initializes object data before setting the data from ajax
 * request.
 * <p>
 * Used for single field editing.
 * 
 * @author rjokelai
 */
@Component("singleFieldEditInterceptor")
public class SingleFieldEditInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1668784370092320107L;

    public String intercept(ActionInvocation invocation) throws Exception {
        if (invocation.getAction() instanceof SingleFieldEditable) {
            SingleFieldEditable action = ((SingleFieldEditable)invocation.getAction());
            
            ActionContext context = invocation.getInvocationContext();
            Map<String, Object> params = context.getParameters();
            int id = 0;
            
            try {
                Object idObj = params.get(action.getIdFieldName());
                String[] idStr = (String[])idObj;
                id = Integer.parseInt(idStr[0]);
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
            
            action.initializeDataForEditing(id);
        }
        return invocation.invoke();
    }
}
