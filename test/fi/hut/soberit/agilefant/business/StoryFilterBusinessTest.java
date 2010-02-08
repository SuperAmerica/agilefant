package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryFilterBusinessImpl;
import fi.hut.soberit.agilefant.model.Label;
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
        StoryFilters storyFilters = new StoryFilters(null, null,
                StoryState.valueSet);

        assertTrue(storyFilterBusiness.filterByState(story1, storyFilters));
    }

    @Test
    public void testFilterByState_FilteredOut() {
        Story story = new Story();
        story.setState(StoryState.DONE);
        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.BLOCKED);
        StoryFilters storyFilters = new StoryFilters(null, null, states);

        assertFalse(storyFilterBusiness.filterByState(story, storyFilters));

    }

    @Test
    public void testFilterByName() {
        Story story = new Story();
        story.setName("dIIbadAAba");
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", null, null);

        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));

    }

    @Test
    public void testFilterByName_notFound() {
        Story story = new Story();
        story.setName("dIIdadAAba");
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", null, null);

        assertFalse(storyFilterBusiness.filterByName(story, storyFilters));

    }

    @Test
    public void testFilterByName_null() {
        Story story = new Story();
        story.setName("dIIbadAAba");
        StoryFilters storyFilters = new StoryFilters(null, null, null);

        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));
    }

    @Test
    public void testFilterByName_length0() {
        Story story = new Story();
        story.setName("");
        StoryFilters storyFilters = new StoryFilters(null, null, null);

        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));
    }

    @Test
    public void testFilterByLabels() {
        Story story = new Story();
        Set<Label> labels2 = new HashSet<Label>();
        Label great = new Label();
        great.setName("Great");
        great.setDisplayName("Great");
        labels2.add(great);
        story.setLabels(labels2);

        Set<String> labels = new HashSet<String>();
        labels.add("great");
        labels.add("tree");
        StoryFilters storyFilters = new StoryFilters(null, labels, null);

        assertTrue(storyFilterBusiness.filterByLabels(story, storyFilters));
    }

    @Test
    public void testFilterByLabels_emptyFilter() {
        Story story = new Story();

        Set<String> labels = new HashSet<String>();
        StoryFilters storyFilters = new StoryFilters(null, labels, null);

        assertTrue(storyFilterBusiness.filterByLabels(story, storyFilters));
    }

    @Test
    public void testFilterByLabels_notMatched() {
        Story story = new Story();

        Set<String> labels = new HashSet<String>();
        labels.add("great");
        StoryFilters storyFilters = new StoryFilters(null, labels, null);

        assertFalse(storyFilterBusiness.filterByLabels(story, storyFilters));
    }

    @Test
    public void testFilterStories() {
        Story failByName = new Story();
        Story failByState = new Story();
        Story failByLabel = new Story();
        Story succeed = new Story();

        succeed.setId(1000);

        failByName.setName("Somethingelse");
        failByState.setName("Name");
        failByLabel.setName("Name");
        succeed.setName("Name");

        failByName.setState(StoryState.NOT_STARTED);
        failByState.setState(StoryState.DONE);
        failByLabel.setState(StoryState.NOT_STARTED);
        succeed.setState(StoryState.NOT_STARTED);

        Label label = new Label();
        label.setName("label");
        label.setDisplayName("Label");
        Set<Label> labels = new HashSet<Label>();
        labels.add(label);

        failByName.setLabels(labels);
        failByState.setLabels(labels);
        failByLabel.setLabels(Collections.<Label> emptySet());
        succeed.setLabels(labels);

        List<Story> stories = Arrays.asList(failByName, failByState,
                failByLabel, succeed);

        Set<String> labelNames = new HashSet<String>();
        labelNames.add("label");

        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.NOT_STARTED);

        StoryFilters storyFilters = new StoryFilters("Name", labelNames, states);
        List<Story> filteredStories = storyFilterBusiness.filterStories(
                stories, storyFilters);

        assertEquals(1, filteredStories.size());
        assertEquals(succeed.getId(), filteredStories.get(0).getId());
    }

    @Test
    public void testFilterStories_recursive() {
        Story story1 = new Story();
        Story childStory1 = new Story();
        Story childChildStory1 = new Story();
        Story childStory2 = new Story();

        story1.setChildren(Arrays.asList(childStory1, childStory2));
        childStory1.setChildren(Arrays.asList(childChildStory1));
        story1.setId(1);
        childStory1.setId(10);
        childStory2.setId(20);
        childChildStory1.setId(100);

        childChildStory1.setState(StoryState.DONE);

        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.DONE);
        StoryFilters storyFilters = new StoryFilters(null,
                new HashSet<String>(), states);
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

}
