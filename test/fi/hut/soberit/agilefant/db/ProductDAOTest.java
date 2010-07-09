package fi.hut.soberit.agilefant.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
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
    
    @Test
    public void testRetrieveLeafStories() {
        executeClassSql();
        Product product = new Product();
        product.setId(1);
        List<Story> actual = this.productDAO.retrieveLeafStories(product);
        List<Integer> storyIds = new ArrayList<Integer>();
        for(Story story : actual) {
            storyIds.add(story.getId());
        }
        assertTrue(storyIds.contains(2));
        assertTrue(storyIds.contains(4));
        assertTrue(storyIds.contains(5));
        assertTrue(storyIds.contains(6));
        assertTrue(storyIds.contains(7));
        assertTrue(storyIds.contains(8));
        assertEquals(6, actual.size());
    }
}
