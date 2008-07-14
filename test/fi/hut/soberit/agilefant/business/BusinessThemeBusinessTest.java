package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashSet;
import java.util.Set;

import fi.hut.soberit.agilefant.business.impl.BusinessThemeBusinessImpl;
import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import junit.framework.TestCase;

public class BusinessThemeBusinessTest extends TestCase {
    
    private BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
    private BusinessThemeDAO themeDAO;
    
    public void testDeleteBusinessThemeWithBlis() {
        themeDAO = createMock(BusinessThemeDAO.class);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        
        int bliId1 = 100;
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(bliId1);       
        int themeId1 = 101;
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(themeId1);
        theme1.setName("foo");
        theme1.setDescription("");
        Set<BacklogItem> items = new HashSet<BacklogItem>();
        items.add(bli1);
        theme1.setBacklogItems(items);
        bli1.getBusinessThemes().add(theme1);
        
        
        // Record expected behavior
        expect(themeDAO.get(themeId1)).andReturn(theme1);
        themeDAO.remove(themeId1);
        replay(themeDAO);
        
        // run method under test
        try {
            themeBusiness.delete(themeId1);
        } catch (ObjectNotFoundException onfe) {
            fail();
        }
        
        // No themes in bli.
        assertEquals(0, bli1.getBusinessThemes().size());
        
        // verify behavior
        verify(themeDAO);
        
    }
    /*
    public void testStoreBusinessTheme() {
        themeDAO = createMock(BusinessThemeDAO.class);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        
        int themeId1 = 101;
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(themeId1);
        theme1.setName("foo");
        theme1.setDescription("");         
        
        // Record expected behavior
        expect(themeDAO.get(themeId1)).andReturn(theme1);        
        themeDAO.store(theme1);        
        replay(themeDAO);
        
        // run method under test
        try {
            themeBusiness.store(themeId1, theme1);           
        } catch (ObjectNotFoundException onfe) {
            fail();
        } catch (Exception e) {
            fail();
        }                        
               
        // verify behavior
        verify(themeDAO);
        
    }
*/
}
