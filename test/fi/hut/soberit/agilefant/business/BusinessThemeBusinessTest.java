package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import fi.hut.soberit.agilefant.business.impl.BusinessThemeBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;

public class BusinessThemeBusinessTest extends TestCase {

    private BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
    private BusinessThemeDAO themeDAO;

    public void testDeleteBusinessThemeWithBlis() {
        themeDAO = createMock(BusinessThemeDAO.class);
        themeBusiness.setBusinessThemeDAO(themeDAO);

        int bliId1 = 100;
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(bliId1);
        int prodId = 20;
        Product prod = new Product();
        prod.setId(prodId);
        int themeId1 = 101;
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(themeId1);
        theme1.setProduct(prod);
        theme1.setName("foo");
        theme1.setDescription("");
        theme1.setActive(true);
        Set<BacklogItem> items = new HashSet<BacklogItem>();
        items.add(bli1);
        theme1.setBacklogItems(items);
        bli1.getBusinessThemes().add(theme1);


        // Record expected behavior
        expect(themeDAO.get(themeId1)).andReturn(theme1);
        themeDAO.remove(themeId1);
        replay(themeDAO);

        // run method under test
        try {
            themeBusiness.delete(themeId1);
        } catch (ObjectNotFoundException onfe) {
            fail();
        }

        // No themes in bli.
        assertEquals(0, bli1.getBusinessThemes().size());

        // verify behavior
        verify(themeDAO);

    }

    /**
     * Add theme binding to iteration, which has no themes associated 
     * with itself or with the parent project.
     */
    public void testAddThemeToBacklog_IterationNew() {

        //setup backlogs
        Project proj = new Project();
        Iteration iter = new Iteration();
        proj.setId(1);
        iter.setId(2);
        List<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add(iter);
        proj.setIterations(iterations);
        iter.setProject(proj);
        //setup theme
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(1);
        //setup services
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogDAO backlogDAO = EasyMock.createMock(BacklogDAO.class);
        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        //expected DAO calls
        expect(backlogDAO.get(2)).andReturn(iter).atLeastOnce();
        expect(themeDAO.get(1)).andReturn(theme1).atLeastOnce();
        //expect one save call - business creates the binding, so only expect correct type
        themeDAO.saveOrUpdateBacklogThemeBinding(EasyMock.isA(BacklogThemeBinding.class));

        replay(themeDAO);
        replay(backlogDAO);

        //run
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "0h");
        verify(themeDAO);
        verify(backlogDAO);


    }

    public void testAddThemeToBacklog_IterationExisting_Formats() {

        //setup backlogs
        Project proj = new Project();
        Iteration iter = new Iteration();
        proj.setId(1);
        iter.setId(2);
        List<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add(iter);
        proj.setIterations(iterations);
        iter.setProject(proj);
        //setup theme
        BusinessTheme theme1 = new BusinessTheme();
        BacklogThemeBinding bind = new BacklogThemeBinding();
        bind.setBacklog(iter);
        bind.setBusinessTheme(theme1);
        List<BacklogThemeBinding> bindings = new ArrayList<BacklogThemeBinding>();
        bindings.add(bind);
        theme1.setBacklogBindings(bindings);
        iter.setBusinessThemeBindings(bindings);
        theme1.setId(1);
        //setup services
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogDAO backlogDAO = EasyMock.createMock(BacklogDAO.class);
        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        //expected DAO calls
        expect(backlogDAO.get(2)).andReturn(iter).atLeastOnce();
        expect(themeDAO.get(1)).andReturn(theme1).atLeastOnce();


        themeDAO.saveOrUpdateBacklogThemeBinding(bind);
        themeDAO.saveOrUpdateBacklogThemeBinding(bind);
        themeDAO.saveOrUpdateBacklogThemeBinding(bind);
        themeDAO.saveOrUpdateBacklogThemeBinding(bind);
        themeDAO.saveOrUpdateBacklogThemeBinding(bind);
        replay(themeDAO);
        replay(backlogDAO);

        //run
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "0h");
        assertEquals(bind.isRelativeBinding(), false);
        assertEquals(bind.getFixedSize().getTime(),0);
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "0%");
        assertEquals(bind.isRelativeBinding(), true);
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "10%");
        assertEquals(bind.isRelativeBinding(), true);
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "1h");
        assertEquals(bind.getFixedSize().getTime(),3600);
        assertEquals(bind.isRelativeBinding(), false);
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "1h 30min");
        assertEquals(bind.isRelativeBinding(), false);
        assertEquals(bind.getFixedSize().getTime(),5400);
        verify(themeDAO);
        verify(backlogDAO);

    }

    /**
     * project has the theme that is being added to the iteration
     */
    public void testAddThemeToBacklog_IterationProject() {
        //setup backlogs
        Project proj = new Project();
        Iteration iter = new Iteration();
        proj.setId(1);
        iter.setId(2);
        List<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add(iter);
        proj.setIterations(iterations);
        iter.setProject(proj);
        //setup theme
        BusinessTheme theme1 = new BusinessTheme();
        BacklogThemeBinding bind = new BacklogThemeBinding();
        bind.setId(1);
        bind.setBusinessTheme(theme1);
        bind.setBacklog(proj);
        List<BacklogThemeBinding> bindings = new ArrayList<BacklogThemeBinding>();
        bindings.add(bind);
        theme1.setBacklogBindings(bindings);
        proj.setBusinessThemeBindings(bindings);
        theme1.setId(1);
        //setup services
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogDAO backlogDAO = EasyMock.createMock(BacklogDAO.class);
        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        //expected DAO calls
        expect(backlogDAO.get(2)).andReturn(iter).atLeastOnce();
        expect(themeDAO.get(1)).andReturn(theme1).atLeastOnce();
        themeDAO.saveOrUpdateBacklogThemeBinding(EasyMock.isA(BacklogThemeBinding.class));
        themeDAO.removeBacklogThemeBinding(bind);

        replay(themeDAO);
        replay(backlogDAO);
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), iter.getId(), "0h");
        verify(themeDAO);
        verify(backlogDAO);
    }
    public void testAddThemeToBacklog_ProjectNew() {
        //setup backlogs
        Project proj = new Project();
        Iteration iter = new Iteration();
        proj.setId(1);
        iter.setId(2);
        List<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add(iter);
        proj.setIterations(iterations);
        iter.setProject(proj);
        //setup theme
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(1);
        //setup services
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogDAO backlogDAO = EasyMock.createMock(BacklogDAO.class);
        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        //expected DAO calls
        expect(backlogDAO.get(1)).andReturn(proj).atLeastOnce();
        expect(themeDAO.get(1)).andReturn(theme1).atLeastOnce();
        //expect one save call - business creates the binding, so only expect correct type
        themeDAO.saveOrUpdateBacklogThemeBinding(EasyMock.isA(BacklogThemeBinding.class));

        replay(themeDAO);
        replay(backlogDAO);

        //run
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), proj.getId(), "0h");
        verify(themeDAO);
        verify(backlogDAO);


    }

    public void testAddThemeToBacklog_ProjectIteration() {
        //setup backlogs
        Project proj = new Project();
        Iteration iter = new Iteration();
        proj.setId(1);
        iter.setId(2);
        List<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add(iter);
        proj.setIterations(iterations);
        iter.setProject(proj);
        //setup theme
        BusinessTheme theme1 = new BusinessTheme();
        BacklogThemeBinding bind = new BacklogThemeBinding();
        bind.setId(1);
        bind.setBusinessTheme(theme1);
        bind.setBacklog(iter);
        List<BacklogThemeBinding> bindings = new ArrayList<BacklogThemeBinding>();
        bindings.add(bind);
        theme1.setBacklogBindings(bindings);
        iter.setBusinessThemeBindings(bindings);
        theme1.setId(1);

        //setup services
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogDAO backlogDAO = EasyMock.createMock(BacklogDAO.class);
        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        //expected DAO calls
        expect(backlogDAO.get(1)).andReturn(proj).atLeastOnce();
        expect(themeDAO.get(1)).andReturn(theme1).atLeastOnce();

        replay(themeDAO);
        replay(backlogDAO);
        //call should save the theme binding, as project's iteration has the theme
        themeBusiness.addOrUpdateThemeToBacklog(theme1.getId(), proj.getId(), "0h");
        verify(themeDAO);
        verify(backlogDAO);
    }

    public void testAddThemeToBacklog_duplicates() {
        Project proj = new Project();
        proj.setBusinessThemeBindings(new ArrayList<BacklogThemeBinding>());
        proj.setId(1);
        //setup theme
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(1);
        //setup services
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogDAO backlogDAO = EasyMock.createMock(BacklogDAO.class);
        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        expect(backlogDAO.get(1)).andReturn(proj);
        expect(themeDAO.get(1)).andReturn(theme1);
        themeDAO.saveOrUpdateBacklogThemeBinding(EasyMock.isA(BacklogThemeBinding.class));

        replay(backlogDAO);
        replay(themeDAO);

        int[] themeIds = {1,1,1,1};
        String[] alloc = {"0","0","0","0"};
        themeBusiness.multipleAddOrUpdateThemeToBacklog(themeIds, 1, alloc);
        verify(backlogDAO);
        verify(themeDAO);

    }   

    public void testAddMultipleThemesToBacklogItem() {
        BacklogItem bli = new BacklogItem();
        BacklogItemBusiness bliBus = EasyMock.createMock(BacklogItemBusiness.class);
        BusinessThemeDAO themeDAO = EasyMock.createMock(BusinessThemeDAO.class);
        BacklogItemDAO bliDAO = EasyMock.createMock(BacklogItemDAO.class);
        BusinessThemeBusinessImpl themeBusiness = new BusinessThemeBusinessImpl();
        themeBusiness.setBacklogItemBusiness(bliBus);
        themeBusiness.setBusinessThemeDAO(themeDAO);
        themeBusiness.setBacklogItemDAO(bliDAO);
        BusinessTheme theme1 = new BusinessTheme();
        BusinessTheme theme2 = new BusinessTheme();
        BusinessTheme theme3 = new BusinessTheme();
        List<BusinessTheme> themes = new ArrayList<BusinessTheme>();
        themes.add(theme1);
        bli.setBusinessThemes(themes);
        expect(bliBus.getBacklogItem(1)).andReturn(bli);
        bliDAO.store(bli);
        expect(themeDAO.get(2)).andReturn(theme2);
        expect(themeDAO.get(3)).andReturn(theme3);

        replay(bliBus);
        replay(themeDAO);
        replay(bliDAO);
        int[] ids = {2,3};
        themeBusiness.addMultipleThemesToBacklogItem(ids, 1);
        assertEquals(2, bli.getBusinessThemes().size());
        verify(themeDAO);
        verify(bliBus);
        verify(bliDAO);

    }

    public void testGetActiveOrSelecterThemes() {
        themeDAO = createMock(BusinessThemeDAO.class);

        themeBusiness.setBusinessThemeDAO(themeDAO);

        BacklogDAO backlogDAO = createMock(BacklogDAO.class);
        BacklogItemDAO backlogItemDAO = createMock(BacklogItemDAO.class);
        ProductDAO productDAO = createMock(ProductDAO.class);

        themeBusiness.setBacklogDAO(backlogDAO);
        themeBusiness.setBacklogItemDAO(backlogItemDAO);
        themeBusiness.setProductDAO(productDAO);
        
        //create product and project
        Product product = new Product();
        product.setName("Test Product");

        //int productId = (Integer) productDAO.create(product);
        //product = productDAO.get(productId);

        // create iteration and bli
        //Backlog backlog = new Iteration();
        Backlog backlog = product;
        //int backlogId = (Integer) backlogDAO.create(backlog);
        //backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        //int bliId = (Integer) backlogItemDAO.create(bli);
        //bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // set bli fields
        bli.setName("Test Name");
        bli.setDescription("Test Description");
        bli.setPriority(Priority.BLOCKER);
        bli.setOriginalEstimate(new AFTime("2h 15min"));
        bli.setState(State.BLOCKED);
        bli.setEffortLeft(new AFTime("2h 15min"));


        // int bliId1 = 100;
        
        int prodId = 20;
        Product prod = new Product();
        prod.setId(prodId);
        int themeId1 = 101;
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(themeId1);
        theme1.setProduct(prod);
        theme1.setName("foo");
        theme1.setDescription("");
        theme1.setActive(true);
        int themeId2 = 102;
        BusinessTheme theme2 = new BusinessTheme();
        theme2.setId(themeId2);
        theme2.setProduct(prod);
        theme2.setName("foo2");
        theme2.setDescription("");
        theme2.setActive(false);
        Set<BusinessTheme> productThemes = new HashSet<BusinessTheme>();
        productThemes.add(theme1);
        productThemes.add(theme2);
        Set<BusinessTheme> itemThemes = new HashSet<BusinessTheme>();
        itemThemes.add(theme2);
        product.setBusinessThemes(productThemes);
        bli.setBusinessThemes(itemThemes);

        expect(backlogItemDAO.get(0)).andReturn(bli);
        expect(backlogItemDAO.get(0)).andReturn(bli);
        expect(backlogItemDAO.get(0)).andReturn(bli);
        replay(backlogItemDAO);
        assertEquals(2, themeBusiness.getBacklogItemActiveOrSelectedThemes(0).size());
        theme1.setActive(false);
        assertEquals(1, themeBusiness.getBacklogItemActiveOrSelectedThemes(0).size());
        BusinessTheme theme3 = new BusinessTheme();
        int themeId3 = 103;
        theme3.setId(themeId3);
        theme3.setProduct(prod);
        theme3.setName("foo3");
        theme3.setDescription("");
        theme3.setActive(true);
        productThemes.add(theme3);
        assertEquals(2, themeBusiness.getBacklogItemActiveOrSelectedThemes(0).size());
        
        verify(backlogItemDAO);
    }
    /*
    public void testStoreBusinessTheme() {
        themeDAO = createMock(BusinessThemeDAO.class);
        themeBusiness.setBusinessThemeDAO(themeDAO);

        int themeId1 = 101;
        BusinessTheme theme1 = new BusinessTheme();
        theme1.setId(themeId1);
        theme1.setName("foo");
        theme1.setDescription("");         

        // Record expected behavior
        expect(themeDAO.get(themeId1)).andReturn(theme1);        
        themeDAO.store(theme1);        
        replay(themeDAO);

        // run method under test
        try {
            themeBusiness.store(themeId1, theme1);           
        } catch (ObjectNotFoundException onfe) {
            fail();
        } catch (Exception e) {
            fail();
        }                        

        // verify behavior
        verify(themeDAO);

    }
     */
    
//    public void getActiveThemesForBacklogAsJSON_noThemes() {
//        productDAO = createMock(IterationGoalDAO.class);
//        iterationGoalBusiness.setIterationGoalDAO(iterationGoalDAO);
//        
//        expect(iterationGoalDAO.get(123)).andReturn(null);
//        replay(iterationGoalDAO);
//        try {
//            iterationGoalBusiness.iterationGoalToJSON(123);
//            fail();
//        }
//        catch (ObjectNotFoundException onfe) {
//        }
//        
//        verify(iterationGoalDAO);
//    }
}
