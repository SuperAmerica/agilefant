package fi.hut.soberit.agilefant.web.tag;


import java.util.Collection;
import java.util.Stack;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.web.PageItem;

public class BreadCrumbTrailTag extends SpringTagSupport {
	

	private static final long serialVersionUID = -8291423940208835187L;
	public static final String PAGE_HIERARCHY = "pageHierarchy";
	private PageItem page = null;
	private Collection<PageItem> hierarchy = new Stack<PageItem>();
	
	@Override
	public int doStartTag() throws JspException {
		if (page != null) {
			hierarchy.add(page);
			traverse(page);
			super.getPageContext().setAttribute(BreadCrumbTrailTag.PAGE_HIERARCHY, hierarchy);			
		}
		
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	private void traverse(PageItem pi) {
		PageItem parent = pi.getParent();
		if (parent != null) { 
			hierarchy.add(parent);
			traverse(parent);
		}
	}

	public PageItem getPage() {
		return page;
	}

	public void setPage(PageItem page) {
		this.page = page;
	}
}
