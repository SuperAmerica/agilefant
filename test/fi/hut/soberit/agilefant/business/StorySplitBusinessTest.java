package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StorySplitBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;

public class StorySplitBusinessTest {

    StorySplitBusinessImpl testable;
    
    StoryDAO storyDAO;
    
    @Before
    public void setUp_dependencies() {
        testable = new StorySplitBusinessImpl();
        
        storyDAO = createStrictMock(StoryDAO.class);
        testable.setStoryDAO(storyDAO);
    }
    
    @Test
    public void testSplitStory() {
        assertEquals(Story.class, testable.splitStory(new Story(),
                Arrays.asList(new Story())).getClass());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSplitStory_emptyList() {
        testable.splitStory(new Story(), new ArrayList<Story>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSplitStory_nullOriginal() {
        testable.splitStory(null, Arrays.asList(new Story()));
    }
}
