package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.ProductBusinessImpl;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class ProductBusinessTest {

    @TestedBean
    private ProductBusinessImpl productBusiness = new ProductBusinessImpl();
    @Mock
    private ProductDAO productDAO;
    @Mock
    private StoryBusiness storyBusiness;
    @Mock
    private IterationBusiness iterationBusiness;
    @Mock
    private ProjectBusiness projectBusiness;
    @Mock
    private HourEntryBusiness hourEntryBusiness;

    private void replayAll() {
        replay(productDAO, storyBusiness, iterationBusiness, projectBusiness,
                hourEntryBusiness);
    }

    private void verifyAll() {
        verify(productDAO, storyBusiness, iterationBusiness, projectBusiness,
                hourEntryBusiness);
    }

    @Test
    @DirtiesContext
    public void testRetrieveAllOrderByName() {
        expect(productDAO.retrieveBacklogTree()).andReturn(
                new ArrayList<Product>());
        replay(productDAO);

        productBusiness.retrieveAllOrderByName();

        verify(productDAO);
    }

    @Test
    @DirtiesContext
    public void testStore() {
        Product prod = new Product();
        prod.setName("Test");
        prod.setDescription("This is a test.");
        prod.setId(1);

        Product newData = new Product();
        newData.setName("New test name");
        newData.setDescription("new description");

        expect(productDAO.get(1)).andReturn(prod);
        productDAO.store(prod);

        replay(productDAO);
        Product actual = productBusiness.store(1, newData);
        assertEquals(newData.getName(), actual.getName());
        assertEquals(newData.getDescription(), actual.getDescription());
        verify(productDAO);
    }

    @Test
    @DirtiesContext
    public void testStore_newProduct() {
        Product prod = new Product();
        prod.setName("Test");
        prod.setDescription("This is a test.");

        expect(productDAO.create(EasyMock.isA(Product.class))).andReturn(1);
        expect(productDAO.get(1)).andReturn(prod);

        replay(productDAO);
        Product actual = productBusiness.store(0, prod);
        assertEquals(prod.getName(), actual.getName());
        assertEquals(prod.getDescription(), actual.getDescription());
        verify(productDAO);
    }

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void testStore_invalidData() {
        Product prod = new Product();
        prod.setName("");
        prod.setDescription("This is a test.");
        productBusiness.store(0, prod);
    }

    @Test
    @DirtiesContext
    public void testDelete() {
        Product prod = new Product();
        prod.setId(10);
        expect(productDAO.get(prod.getId())).andReturn(prod);
        productDAO.remove(prod);
    }

    @Test
    @DirtiesContext
    public void testDelete_withStories() {
        Product prod = new Product();
        prod.setId(10);
        Story story1 = new Story();
        Story story2 = new Story();
        story1.setBacklog(prod);
        story2.setBacklog(prod);
        prod.getStories().add(story1);
        prod.getStories().add(story2);
        expect(productDAO.get(prod.getId())).andReturn(prod);
        storyBusiness.forceDelete(story1);
        storyBusiness.forceDelete(story2);
        hourEntryBusiness.deleteAll(prod.getHourEntries());
        productDAO.remove(prod);
        replayAll();
        productBusiness.delete(prod.getId());
        verifyAll();

    }

    @Test
    @DirtiesContext
    public void testDelete_withBacklogItems() {
        Product prod = new Product();
        prod.setId(10);
        Project project = new Project();
        Iteration iteration = new Iteration();
        project.setParent(prod);
        iteration.setParent(prod);
        prod.getChildren().add(project);
        prod.getChildren().add(iteration);
        expect(productDAO.get(prod.getId())).andReturn(prod);
        projectBusiness.delete(project.getId());
        iterationBusiness.delete(iteration.getId());
        hourEntryBusiness.deleteAll(prod.getHourEntries());
        productDAO.remove(prod);
        replayAll();
        productBusiness.delete(prod.getId());
        verifyAll();

    }

    @Test
    @DirtiesContext
    public void testDelete_withHourEntries() {
        Product prod = new Product();
        prod.setId(10);
        Set<BacklogHourEntry> hourEntries = new HashSet<BacklogHourEntry>();
        BacklogHourEntry hourEntry = new BacklogHourEntry();
        BacklogHourEntry hourEntry2 = new BacklogHourEntry();
        hourEntry.setId(1);
        hourEntry2.setId(2);
        hourEntries.add(hourEntry);
        hourEntries.add(hourEntry2);
        prod.setHourEntries(hourEntries);
        expect(productDAO.get(prod.getId())).andReturn(prod);
        hourEntryBusiness.deleteAll(hourEntries);
        productDAO.remove(prod);
        replayAll();
        productBusiness.delete(prod.getId());
        verifyAll();
    }

}
