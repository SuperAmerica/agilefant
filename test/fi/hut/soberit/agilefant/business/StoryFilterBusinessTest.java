package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryFilterBusinessImpl;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.util.StoryFilters;

public class StoryFilterBusinessTest {

    private StoryFilterBusinessImpl storyFilterBusiness;

    @Before
    public void setUp() {
        storyFilterBusiness = new StoryFilterBusinessImpl();
    }

    @Test
    public void testFilterByState() {
        Story story1 = new Story();
        story1.setState(StoryState.IMPLEMENTED);
        StoryFilters storyFilters = new StoryFilters(null,
                StoryState.valueSet);

        assertTrue(storyFilterBusiness.filterByState(story1, storyFilters));
    }

    @Test
    public void testFilterByState_FilteredOut() {
        Story story = new Story();
        story.setState(StoryState.DONE);
        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.BLOCKED);
        StoryFilters storyFilters = new StoryFilters(null, states);

        assertFalse(storyFilterBusiness.filterByState(story, storyFilters));

    }

    @Test
    public void testFilterByName() {
        Story story = new Story();
        story.setName("dIIbadAAba");
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", null);

        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));

    }

    @Test
    public void testFilterByName_notFound() {
        Story story = new Story();
        story.setName("dIIdadAAba");
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", null);

        assertFalse(storyFilterBusiness.filterByName(story, storyFilters));

    }

    @Test
    public void testFilterByLabels() {
        Story story = new Story();
        Set<Label> labels2 = new HashSet<Label>();
        Label great = new Label();
        great.setName("great");
        great.setDisplayName("Great");
        labels2.add(great);
        story.setLabels(labels2);
        
        assertTrue(storyFilterBusiness.filterByLabels(story, "great"));
    }

    @Test
    public void testFilterByLabels_notMatched() {
        Story story = new Story();

        assertFalse(storyFilterBusiness.filterByLabels(story, "great"));
    }

    @Test
    public void testFilterStories() {
        Story failByName = new Story();
        Story failByState = new Story();
        Story succeed = new Story();

        succeed.setId(1000);

        failByName.setName("Somethingelse");
        failByState.setName("Name");
        succeed.setName("Name");

        failByName.setState(StoryState.NOT_STARTED);
        failByState.setState(StoryState.DONE);
        succeed.setState(StoryState.NOT_STARTED);

        List<Story> stories = Arrays.asList(failByName, failByState,
                succeed);

        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.NOT_STARTED);

        StoryFilters storyFilters = new StoryFilters("Name", states);
        List<Story> filteredStories = storyFilterBusiness.filterStories(
                stories, storyFilters);

        assertEquals(1, filteredStories.size());
        assertEquals(succeed.getId(), filteredStories.get(0).getId());
    }

    @Test
    public void testFilterStories_recursive() {
        Backlog bl = new Project();
        Story story1 = new Story();
        story1.setBacklog(bl);
        Story childStory1 = new Story();
        childStory1.setBacklog(bl);
        Story childChildStory1 = new Story();
        childChildStory1.setBacklog(bl);
        Story childStory2 = new Story();
        childStory2.setBacklog(bl);

        story1.setChildren(Arrays.asList(childStory1, childStory2));
        childStory1.setChildren(Arrays.asList(childChildStory1));
        story1.setId(1);
        childStory1.setId(10);
        childStory2.setId(20);
        childChildStory1.setId(100);

        childChildStory1.setState(StoryState.DONE);

        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.DONE);
        StoryFilters storyFilters = new StoryFilters(null, states);
        List<Story> filteredStories = storyFilterBusiness.filterStories(Arrays
                .asList(story1), storyFilters);
        assertEquals(1, filteredStories.size());
        Story filteredStory = filteredStories.get(0);
        assertEquals(story1.getId(), filteredStory.getId());
        assertEquals(1, filteredStory.getChildren().size());
        Story filteredChildStory = filteredStory.getChildren().get(0);
        assertEquals(childStory1.getId(), filteredChildStory.getId());
        assertEquals(1, filteredChildStory.getChildren().size());
        Story filteredChildChildStory = filteredChildStory.getChildren().get(0);
        assertEquals(childChildStory1.getId(), filteredChildChildStory.getId());
    }
    
    @Test
    public void testFilterStoryList() {
        Backlog backlog = new Project();
        backlog.setName("?");
        Story story1 = new Story();
        story1.setBacklog(backlog);
        Story story2 = new Story();
        story2.setBacklog(backlog);
        story2.setName("test");
        List<Story> stories = Arrays.asList(story1, story2);
        StoryFilters filter = new StoryFilters("test", null);
        assertEquals(story2, storyFilterBusiness.filterStoryList(stories, filter).get(0));
    }

}
