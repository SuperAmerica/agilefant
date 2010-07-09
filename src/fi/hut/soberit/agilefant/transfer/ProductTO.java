package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class ProductTO extends Product {

    public ProductTO(Product product) {
        BeanCopier.copy(product, this);
    }
}
