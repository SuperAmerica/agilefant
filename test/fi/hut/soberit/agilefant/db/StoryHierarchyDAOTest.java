package fi.hut.soberit.agilefant.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import static org.junit.Assert.*;

@ContextConfiguration
@Transactional
public class StoryHierarchyDAOTest extends AbstractHibernateTests {
    @Autowired
    private StoryHierarchyDAO testable;
    
    @Test
    public void testRetrieveProjectRootStories() {
        executeClassSql();
        Set<Integer> actualStoryIds = new HashSet<Integer>();
        Project project = new Project();
        project.setId(2);
        List<Story> actual = this.testable.retrieveProjectRootStories(project);
        assertEquals(4, actual.size());
        for(Story story : actual) {
            actualStoryIds.add(story.getId());
        }
       assertTrue(actualStoryIds.contains(21));
       assertTrue(actualStoryIds.contains(24));
       assertTrue(actualStoryIds.contains(33));
       assertTrue(actualStoryIds.contains(34));

    }
    
    @Test
    public void testRetrieveProjectLeafStories() {
        executeClassSql();
        Set<Integer> actualStoryIds = new HashSet<Integer>();
        Project project = new Project();
        project.setId(2);
        List<Story> actual = this.testable.retrieveProjectLeafStories(project);
        assertEquals(5, actual.size());
        for(Story story : actual) {
            actualStoryIds.add(story.getId());
        }
       assertTrue(actualStoryIds.contains(24));
       assertTrue(actualStoryIds.contains(31));
       assertTrue(actualStoryIds.contains(32));
       assertTrue(actualStoryIds.contains(33));
       assertTrue(actualStoryIds.contains(34));

    }
}
