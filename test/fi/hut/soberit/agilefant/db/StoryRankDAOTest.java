package fi.hut.soberit.agilefant.db;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

import static org.junit.Assert.*;

@ContextConfiguration
@Transactional
public class StoryRankDAOTest extends AbstractHibernateTests {

    @Autowired
    private StoryRankDAO storyRankDAO;


    @Test
    public void testGetByStoryAndBacklog() {
        executeClassSql();
        Backlog backlog = new Project();
        backlog.setId(1);
        Story story = new Story();
        story.setId(1);
        StoryRank actual = storyRankDAO.retrieveByBacklogAndStory(backlog,
                story);
        assertEquals(1, actual.getId());
    }
    
    @Test
    public void testGetIterationRanksForStories() {
        executeClassSql();
        Story story1 = new Story();
        Story story2 = new Story();
        Story story3 = new Story();
        Story story4 = new Story();
        story1.setId(1);
        story2.setId(2);
        story3.setId(3);
        story4.setId(4);
        
        Collection<StoryRank> actual = storyRankDAO.getIterationRanksForStories(Arrays.asList(story1, story2, story3, story4));
                
        assertEquals(4, actual.size());
        assertTrue(checkRankExists(actual, 1, 100, 0));
        assertTrue(checkRankExists(actual, 2, 100, 1));
        assertTrue(checkRankExists(actual, 3, 101, 0));
        assertTrue(checkRankExists(actual, 4, 101, 1));
    }

    private boolean checkRankExists(Collection<StoryRank> actual, int storyId, int backlogId, int rank) {
        for (StoryRank sr : actual) {
            if (sr.getStory().getId() == storyId && sr.getBacklog().getId() == backlogId && sr.getRank() == rank) {
                return true;
            }
        }
        return false;
    }
}