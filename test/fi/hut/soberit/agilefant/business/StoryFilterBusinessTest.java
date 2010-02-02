package fi.hut.soberit.agilefant.business;

import java.util.HashSet;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class StoryFilterBusinessTest {
    
    @Test
    public void filterByStates() {
        Story story1 = new Story();
        Story story2 = new Story();
        
        story1.setState(StoryState.IMPLEMENTED);
        story2.setState(StoryState.DONE);
        
        HashSet<Story> stories = new HashSet<Story>();
        stories.add(story1);
        stories.add(story2);
        Set<StoryState> statesToKeep = new HashSet<StoryState>();
        
        //stateFilters.setImplemented(false);
        //StoryFilterBusiness.filterByStates(stories, statesToKeep);
    }
}
