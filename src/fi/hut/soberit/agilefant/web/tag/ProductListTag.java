package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.ProductBusiness;

public class ProductListTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729034L;

    public static final String PRODUCT_LIST_KEY = "productList";

    private ProductBusiness productBusiness;

    @Override
    public int doStartTag() throws JspException {
        productBusiness = requireBean("productBusiness");
        super.getPageContext().setAttribute(ProductListTag.PRODUCT_LIST_KEY,
                productBusiness.retrieveAllOrderByName());
        return Tag.EVAL_BODY_INCLUDE;
    }

}
