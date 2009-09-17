package fi.hut.soberit.agilefant.integration;

import org.junit.Test;

/**
 * Integration test class for story splitting.
 * <p>
 * Must use the jUnit 3 notation, because DAO tests
 * do not yet support jUnit 4.
 * 
 * @author rjokelai
 */
public class StorySplitIntegrationTest {
    @Test
    public void testDummy() {
        
    }
}
/*
@ContextConfiguration
@Transactional
public class StorySplitIntegrationTest extends AbstractHibernateTests {
   
    @Autowired
    private StorySplitBusiness storySplitBusiness;
    
    @Autowired
    private StoryDAO storyDAO;
    
    @Before
    public void setUp() {
        executeClassSql();       
    }
    
    @Test
    public void testSplitStory_get() {
        Story actual = storySplitBusiness.getStory(1);        
        assertEquals("Persisted story", actual.getName());
    }

}
*/