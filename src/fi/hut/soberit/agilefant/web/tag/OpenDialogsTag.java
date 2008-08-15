package fi.hut.soberit.agilefant.web.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.web.SessionAction;

public class OpenDialogsTag extends SpringTagSupport {

    private static final long serialVersionUID = -8291423940208835187L;

    private String context;

    @SuppressWarnings("unchecked")
    @Override
    public int doStartTag() throws JspException {
        Collection<Integer[]> openDialogs = new ArrayList<Integer[]>();
        Map<String,Map> ajaxContext;
        try {
            ajaxContext = (Map<String,Map>)super.getPageContext().getSession().getAttribute(SessionAction.CONTEXT_KEY);
            if(ajaxContext != null) {
                Map<Integer,Integer> contextData = ajaxContext.get(context);
                if(contextData != null) {
                    for(Integer objectId : contextData.keySet()) {
                        Integer tabId = contextData.get(objectId);
                        openDialogs.add(new Integer[]{objectId, tabId});
                    }
                    
                }
            }
        }
        catch (Exception e) {
            //System.err.println(e.getStackTrace());
            
        }
        
        super.getPageContext().setAttribute(super.getId(), openDialogs);
        
        return Tag.EVAL_BODY_INCLUDE;
    }

    
    public void setContext(String context) {
        this.context = context;
    }
}
