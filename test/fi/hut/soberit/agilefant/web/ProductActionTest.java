package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.model.Product;

public class ProductActionTest extends TestCase {

    ProductAction productAction = new ProductAction();
    ProductBusiness productBusiness;
    
    @Before
    public void setUp() {
        productBusiness = createMock(ProductBusiness.class);
        productAction.setProductBusiness(productBusiness);
    }
    
    @Test
    public void testGetAllProductsAsJSON_interaction() {
        Collection<Product> allProductList = Arrays.asList(new Product());
        
        expect(productBusiness.retrieveAll()).andReturn(allProductList);
        replay(productBusiness);
        
        productAction.getAllProductsAsJSON();
        
        verify(productBusiness);
    }
}
