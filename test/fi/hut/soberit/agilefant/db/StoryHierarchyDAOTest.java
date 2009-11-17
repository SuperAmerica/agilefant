package fi.hut.soberit.agilefant.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import static org.junit.Assert.*;

@ContextConfiguration
@Transactional
public class StoryHierarchyDAOTest extends AbstractHibernateTests {
    @Autowired
    private StoryHierarchyDAO testable;
    private Project project;
    private Project emptyProject;
    private Product product;
    private Product emptyProduct;
    
    @Before
    public void setUp_data() {
        product = new Product();
        product.setId(1);
        emptyProduct = new Product();
        emptyProduct.setId(11);
        project = new Project();
        project.setId(2);
        emptyProject = new Project();
        emptyProject.setId(5);
    }
    
    @Test
    public void testRetrieveProjectRootStories() {
        executeClassSql();
        Set<Integer> actualStoryIds = new HashSet<Integer>();
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
    
    @Test
    public void testRetrieveProjectRootStories_empty() {
        executeClassSql();
        List<Story> actual = this.testable.retrieveProjectRootStories(emptyProject);
        assertEquals(0, actual.size());

    }
    
    @Test
    public void testRetrieveProjectLeafStories_empty() {
        executeClassSql();
        List<Story> actual = this.testable.retrieveProjectLeafStories(emptyProject);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testTotalLeafStoryPoints() {
        executeClassSql();
        assertEquals(140l, this.testable.totalLeafStoryPoints(project));
    }
    @Test
    public void testTotaDonelLeafStoryPoints() {
        executeClassSql();
        assertEquals(30l, this.testable.totalLeafDoneStoryPoints(project));
    }
    @Test
    public void testTotalRootStoryPoints() {
        executeClassSql();
        assertEquals(120l, this.testable.totalRootStoryPoints(project));
    }
    @Test
    public void testTotalLeafStoryPoints_empty() {
        executeClassSql();
        assertEquals(0l, this.testable.totalLeafStoryPoints(emptyProject));
    }
    @Test
    public void testTotaDonelLeafStoryPoints_empty() {
        executeClassSql();
        assertEquals(0l, this.testable.totalLeafDoneStoryPoints(emptyProject));
    }
    @Test
    public void testTotalRootStoryPoints_empty() {
        executeClassSql();
        assertEquals(0l, this.testable.totalRootStoryPoints(emptyProject));
    }
    
    @Test
    public void testRetrieveProductRootStories() {
        executeClassSql();
        Set<Integer> actualStoryIds = new HashSet<Integer>();
        List<Story> actual = this.testable.retrieveProductRootStories(product);
        assertEquals(2, actual.size());
        for(Story story : actual) {
            actualStoryIds.add(story.getId());
        }
       assertTrue(actualStoryIds.contains(11));
       assertTrue(actualStoryIds.contains(14));
    }
    
    @Test
    public void testRetrieveProductRootStories_emptyProduct() {
        executeClassSql();
        List<Story> actual = this.testable.retrieveProductRootStories(emptyProduct);
        assertEquals(0, actual.size());
    }
}
