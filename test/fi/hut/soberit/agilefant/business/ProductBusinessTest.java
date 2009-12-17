package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

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
        expect(productDAO.retrieveBacklogTree()).andReturn(
                new ArrayList<Product>());
        replay(productDAO);

        productBusiness.retrieveAllOrderByName();

        verify(productDAO);
    }

    @Test
    public void testStore() {
        Product prod = new Product();
        prod.setName("Test");
        prod.setDescription("This is a test.");
        prod.setId(1);

        Product newData = new Product();
        newData.setName("New test name");
        newData.setDescription("new description");

        expect(productDAO.get(1)).andReturn(prod);
        productDAO.store(prod);

        replay(productDAO);
        Product actual = productBusiness.store(1, newData);
        assertEquals(newData.getName(), actual.getName());
        assertEquals(newData.getDescription(), actual.getDescription());
        verify(productDAO);
    }

    @Test
    public void testStore_newProduct() {
        Product prod = new Product();
        prod.setName("Test");
        prod.setDescription("This is a test.");

        expect(productDAO.create(EasyMock.isA(Product.class))).andReturn(1);
        expect(productDAO.get(1)).andReturn(prod);

        replay(productDAO);
        Product actual = productBusiness.store(0, prod);
        assertEquals(prod.getName(), actual.getName());
        assertEquals(prod.getDescription(), actual.getDescription());
        verify(productDAO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStore_invalidData() {
        Product prod = new Product();
        prod.setName("");
        prod.setDescription("This is a test.");
        productBusiness.store(0, prod);
    }

}
