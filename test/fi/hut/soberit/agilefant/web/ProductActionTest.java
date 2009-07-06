package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

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
    
    private void replayAll() {
        replay(productBusiness);
    }
    
    private void verifyAll() {
        verify(productBusiness);
    }

    @Test
    public void testRetrieveAll() {
        Collection<Product> allProductList = Arrays.asList(new Product());
        
        expect(productBusiness.retrieveAll()).andReturn(allProductList);
        replayAll();
        
        assertEquals(Action.SUCCESS, productAction.retrieveAll());
        assertEquals(allProductList, productAction.getProducts());
        
        verifyAll();
    }




}
