package fi.hut.soberit.agilefant.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class ResolveSearchResultActionTest extends MockedTestCase {

    @TestedBean
    private ResolveSearchResultAction resolveSearchResultAction;
    @Mock
    private StoryBusiness storyBusiness;
    
    @Test
    @DirtiesContext
    public void testExecute_storyInIteration() {
        Story story = new Story();
        story.setBacklog(new Iteration());
        resolveSearchResultAction.setTargetClassName(Story.class.getCanonicalName());
        resolveSearchResultAction.setTargetObjectId(15);
        
        expect(storyBusiness.retrieve(15)).andReturn(story);
        replayAll();
        assertEquals("iteration", resolveSearchResultAction.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testExecute_storyInProject() {
        Story story = new Story();
        story.setBacklog(new Project());
        resolveSearchResultAction.setTargetClassName(Story.class.getCanonicalName());
        resolveSearchResultAction.setTargetObjectId(15);
        
        expect(storyBusiness.retrieve(15)).andReturn(story);
        replayAll();
        assertEquals("project", resolveSearchResultAction.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testExecute_storyInProduct() {
        Story story = new Story();
        story.setBacklog(new Product());
        resolveSearchResultAction.setTargetClassName(Story.class.getCanonicalName());
        resolveSearchResultAction.setTargetObjectId(15);
        
        expect(storyBusiness.retrieve(15)).andReturn(story);
        replayAll();
        assertEquals("product", resolveSearchResultAction.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testExecute_iteration() {
        resolveSearchResultAction.setTargetClassName(Iteration.class.getCanonicalName());
        resolveSearchResultAction.setTargetObjectId(15);
        replayAll();
        assertEquals("iteration", resolveSearchResultAction.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testExecute_project() {
        resolveSearchResultAction.setTargetClassName(Project.class.getCanonicalName());
        resolveSearchResultAction.setTargetObjectId(15);
        replayAll();
        assertEquals("project", resolveSearchResultAction.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testExecute_product() {
        resolveSearchResultAction.setTargetClassName(Product.class.getCanonicalName());
        resolveSearchResultAction.setTargetObjectId(15);
        replayAll();
        assertEquals("product", resolveSearchResultAction.execute());
        verifyAll();
    }
    
    
}
