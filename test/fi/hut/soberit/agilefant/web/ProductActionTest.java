package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
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
    Product product;

    @Before
    public void setUp() {
        productBusiness = createMock(ProductBusiness.class);
        productAction.setProductBusiness(productBusiness);
    }

    @Before
    public void initData() {
        product = new Product();
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

    @Test
    public void testRetrieve() {
        productAction.setProductId(1);
        expect(productBusiness.retrieve(1)).andReturn(product);
        replayAll();
        productAction.retrieve();
        assertEquals(product, productAction.getProduct());
        verifyAll();
    }

    @Test
    public void testDelete() {
        productAction.setProductId(1);
        productBusiness.delete(1);
        replayAll();
        productAction.delete();
        verifyAll();
    }

    @Test
    public void testStore() {
        productAction.setProductId(1);
        productAction.setProduct(product);
        expect(productBusiness.store(1, product)).andReturn(product);
        replayAll();
        productAction.store();
        assertEquals(product, productAction.getProduct());
        verifyAll();
    }

}
