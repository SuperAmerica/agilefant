package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import fi.hut.soberit.agilefant.db.ProductDAO;

public class ProductListTag extends TagSupport{
	
	private ProductDAO productDAO;
	private PageContext pageContext;
		
	@Override
	public int doStartTag() throws JspException {
		pageContext.setAttribute("productList", productDAO.getAll());
		return Tag.EVAL_BODY_INCLUDE;
	}

	@Override
	public void setPageContext(PageContext ctx) {
		this.pageContext = ctx;
		productDAO = (ProductDAO)this.getApplicationContext(ctx).getBean("productDAO");
		super.setPageContext(ctx);
	}
	
	protected ApplicationContext getApplicationContext(PageContext pageContext){
		return WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
	}
}
