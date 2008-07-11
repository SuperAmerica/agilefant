package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Product;

public interface TimelineBusiness {

    /**
     * Get the product's contents and convert them to JSON.
     * <p>
     * Will fetch the underlying projects, themes and iterations.
     * @param product
     * @return JSON as string
     */
    public String productContentsToJSON(Product product) throws ObjectNotFoundException;
    
    /**
     * Get the product's contents and convert them to JSON.
     * <p>
     * Will fetch the underlying projects, themes and iterations.
     * @param productId
     * @return JSON as string
     */
    public String productContentsToJSON(int productId) throws ObjectNotFoundException;
}
