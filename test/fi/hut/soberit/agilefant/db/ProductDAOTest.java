package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class ProductDAOTest extends AbstractHibernateTests {

    @Autowired
    private ProductDAO productDAO;
    
    @Test
    public void testRetrieveBacklogTree() {
        executeClassSql();
        List<Product> actual = this.productDAO.retrieveBacklogTree();
        assertEquals(2, actual.size());    
    }
}
