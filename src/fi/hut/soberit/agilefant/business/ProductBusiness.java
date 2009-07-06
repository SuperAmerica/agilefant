package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Product;

public interface ProductBusiness extends GenericBusiness<Product> {

    Collection<Product> retrieveAllOrderByName();

}
