package fi.hut.soberit.agilefant.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class BreadCrumbTrailTag extends SpringTagSupport {
	

	private Object obj;
	
	@Override
	public int doStartTag() throws JspException {
		try {
		if(obj instanceof Deliverable) {
			Deliverable d = (Deliverable)obj;
					super.getPageContext().getOut().write("&gt; " + d.getProduct().getName());
		} else {
			super.getPageContext().getOut().write("null");
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Tag.EVAL_BODY_INCLUDE;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
}
