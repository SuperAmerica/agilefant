package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.LabelBusinessImpl;
import fi.hut.soberit.agilefant.db.LabelDAO;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class LabelBusinessTest extends MockedTestCase {
    
    @TestedBean
    private LabelBusinessImpl labelBusiness;
    @Mock
    private LabelDAO labelDAO;
    @Mock
    private StoryBusiness storyBusiness;
    
    @Test
    @DirtiesContext
    public void testLookUpLabelsLike() {
        List<Label> list = new LinkedList<Label>();
        expect(labelDAO.lookupLabelsLike("Notfound")).andReturn(list);
        replay(labelDAO);
        labelBusiness.lookupLabelsLike("Notfound");
        verify(labelDAO);
    }
    
    @Test
    @DirtiesContext
    public void testCreateStoryLabels() {
        User user = new User();
        
        this.clearLoggedInUser();
        this.setCurrentUser(user);
        
        List<String> labelNames = Arrays.asList("foo","faa");
        Story addee = new Story();
        
        Label l1 = new Label();
        
        expect(storyBusiness.retrieve(1)).andReturn(addee);
        expect(labelDAO.labelExists("foo", addee)).andReturn(false);
        expect(labelDAO.labelExists("faa", addee)).andReturn(true);
        Capture<Label> capt = new Capture<Label>();
        expect(labelDAO.create(EasyMock.capture(capt))).andReturn(1);
        expect(labelDAO.get(1)).andReturn(l1);
        
        replayAll();
        labelBusiness.createStoryLabels(labelNames, 1);
        verifyAll();
        
        assertEquals(user, capt.getValue().getCreator());
        assertEquals("foo", capt.getValue().getDisplayName());
        assertTrue(addee.getLabels().contains(l1));        
        
    }

}
