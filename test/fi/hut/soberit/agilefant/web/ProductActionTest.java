package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Collection;

import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.model.Product;

public class ProductActionTest {

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
