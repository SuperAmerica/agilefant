package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import fi.hut.soberit.agilefant.db.ProductDAO;

public class ProductListTag extends SpringTagSupport{
	
	private ProductDAO productDAO;
		
	@Override
	public int doStartTag() throws JspException {
		productDAO = (ProductDAO)super.getApplicationContext().getBean("productDAO");
		super.getPageContext().setAttribute("productList", productDAO.getAll());
		return Tag.EVAL_BODY_INCLUDE;
	}
}
