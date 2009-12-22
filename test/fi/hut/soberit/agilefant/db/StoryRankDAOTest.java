package fi.hut.soberit.agilefant.db;

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
}