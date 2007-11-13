package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.DeliverableDAO;

public class DeliverableListTag extends SpringTagSupport {
	private static final long serialVersionUID = -7498764572144094830L;

	private DeliverableDAO deliverableDAO;

	@Override
	public int doStartTag() throws JspException {
		deliverableDAO = (DeliverableDAO) super.getApplicationContext()
				.getBean("deliverableDAO");
		super.getPageContext().setAttribute(super.getId(),
				deliverableDAO.getAll());
		return Tag.EVAL_BODY_INCLUDE;
	}
}
