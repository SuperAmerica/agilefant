package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

import static org.junit.Assert.*;

@ContextConfiguration
@Transactional
public class WidgetCollectionDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private WidgetCollectionDAO testable;
    @Autowired
    private UserDAO userDAO;
    
    private User user;
    private User userWithNoCollections;
    
    @Before
    public void setUp_data() {
        executeClassSql();
        
        user = userDAO.get(1);
        userWithNoCollections = userDAO.get(2);
    }
    
    @Test
    public void getCollectionsForUser_null() {
        List<WidgetCollection> list = testable.getCollectionsForUser(null);
        
        assertEquals(2, list.size());
        // Check for alphabetical order
        assertEquals("A: Second", list.get(0).getName());
        assertEquals("B: First", list.get(1).getName());
    }
    
    @Test
    public void getCollectionsForUser_first() {
        List<WidgetCollection> list = testable.getCollectionsForUser(user);
        assertEquals(1, list.size());
        assertEquals("C: User first", list.get(0).getName());
    }
    
    @Test
    public void getCollectionsForUser_second() {
        List<WidgetCollection> list = testable.getCollectionsForUser(userWithNoCollections);
        assertEquals(0, list.size());
    }
    

}
