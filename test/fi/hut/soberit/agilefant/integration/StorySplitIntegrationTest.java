package fi.hut.soberit.agilefant.integration;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StorySplitBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

/**
 * Integration test class for story splitting.
 * <p>
 * Must use the jUnit 3 notation, because DAO tests
 * do not yet support jUnit 4.
 * 
 * @author rjokelai
 */
@ContextConfiguration
@Transactional
public class StorySplitIntegrationTest extends AbstractHibernateTests {
   
    @Autowired
    private StorySplitBusiness stb;
    
    @Autowired
    private StoryDAO storyDAO;
    
    public void setUp() {
        executeClassSql();       
    }
    
    public void testSplitStory_get() {
        Story actual = stb.getStory(1);        
        assertEquals("Persisted story", actual.getName());
    }

}