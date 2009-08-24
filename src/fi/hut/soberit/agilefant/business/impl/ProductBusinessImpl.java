package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

@Service("productBusiness")
@Transactional
public class ProductBusinessImpl extends GenericBusinessImpl<Product> implements
        ProductBusiness {

    private ProductDAO productDAO;

    @Autowired
    public void setProductDAO(ProductDAO productDAO) {
        this.genericDAO = productDAO;
        this.productDAO = productDAO;
    }

    @Transactional(readOnly = true)
    public Collection<Product> retrieveAllOrderByName() {
        return productDAO.getAllOrderByName();
    }

    public Product store(int productId, Product productData) {
        this.validateProductData(productData);
        Product storable = new Product();
        if (productId > 0) {
            storable = this.retrieve(productId);
        }
        storable.setName(productData.getName());
        storable.setDescription(productData.getDescription());
        if (storable.getId() > 0) {
            this.store(storable);
            return storable;
        } else {
            int createdId = this.create(storable);
            return this.retrieve(createdId);
        }
    }

    public void validateProductData(Product productData)
            throws IllegalArgumentException {
        if (productData.getName() == null
                || productData.getName().trim().length() == 0) {
            throw new IllegalArgumentException("product.emptyName");
        }
    }
}
