package fi.hut.soberit.agilefant.business.impl;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;

public class ProductBusinessImpl implements ProductBusiness {

    private ProductDAO productDAO;

    public int count() {
        return productDAO.count();
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

}
