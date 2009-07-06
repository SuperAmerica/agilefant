package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

import fi.hut.soberit.agilefant.business.impl.ProductBusinessImpl;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

public class ProductBusinessTest {

    private ProductBusinessImpl productBusiness = new ProductBusinessImpl();
    private ProductDAO productDAO;
    
    @Before 
    public void setUp() {
        productDAO = createMock(ProductDAO.class);
        productBusiness.setProductDAO(productDAO);
    }
    
    @Test
    public void testRetrieveAllOrderByName() {
        expect(productDAO.getAllOrderByName()).andReturn(new ArrayList<Product>());
        replay(productDAO);
        
        productBusiness.retrieveAllOrderByName();
        
        verify(productDAO);
    }
    
}
