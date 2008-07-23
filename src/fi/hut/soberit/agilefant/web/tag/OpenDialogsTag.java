package fi.hut.soberit.agilefant.web.tag;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class OpenDialogsTag extends SpringTagSupport {

    private static final long serialVersionUID = -8291423940208835187L;

    private String context;

    @SuppressWarnings("unchecked")
    @Override
    public int doStartTag() throws JspException {
        Collection<Integer> openDialogs = new ArrayList<Integer>();
        try {
            openDialogs = (Collection<Integer>)super.getPageContext().getSession().getAttribute(context);
        }
        catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        
        super.getPageContext().setAttribute(super.getId(), openDialogs);
        
        return Tag.EVAL_BODY_INCLUDE;
    }

    
    public void setContext(String context) {
        this.context = context;
    }
}
