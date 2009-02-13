package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.TimesheetBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;

public class TimesheetBusinessTest extends TestCase {
    private Product product1, product2;
    private Project project1, project2;
    private Iteration iteration1, iteration2, iteration3;
    private ArrayList<BacklogItem> bliList;
    private ArrayList<HourEntry> heList; 
    private User user1, user2, user3;
    private HourEntryBusiness hourEntryBusiness;
    private BacklogDAO backlogDAO;
    private TimesheetBusinessImpl timesheetBusiness;
    private Map<Integer, Backlog> backlogs = new HashMap<Integer, Backlog>();
    
    public void setUp() {
        timesheetBusiness = new TimesheetBusinessImpl();
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        backlogDAO = createMock(BacklogDAO.class);
        timesheetBusiness = new TimesheetBusinessImpl();
        createData();
        timesheetBusiness.setHourEntryBusiness(hourEntryBusiness);
        timesheetBusiness.setBacklogDAO(backlogDAO);
    }
    
    /**
     * Create a forest of backlogs, backlog items and hour entries. Tests are based on the structure.
     */
    public void createData(){
        product1 = setUpProduct(1);
        backlogs.put(1,product1);
        product2 = setUpProduct(2);
        backlogs.put(2,product2);
        project1 = setUpProject(product2, 3);
        backlogs.put(3, project1);
        project2 = setUpProject(product2, 4);
        backlogs.put(4, project2);
        iteration1 = setUpIteration(project1, 5);
        backlogs.put(5,iteration1);
        iteration2 = setUpIteration(project1, 6);
        backlogs.put(5, iteration2);
        iteration3 = setUpIteration(project1, 7);
        backlogs.put(7,iteration3);
        createUsers();
        createBacklogItems();
        createHourEntries();
        timesheetBusiness.setBacklogDAO(backlogDAO);
        timesheetBusiness.setHourEntryBusiness(hourEntryBusiness);
    }
    

    /**
     * TreeGeneration test for testCompareTrees1.
     */
    private void treeGeneration1(int id1, int id2) {
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(id1);
        backlogIds.add(id2);
        HashSet<Integer> set = new HashSet<Integer>();
        initializeWithFilters(product1, null, null, null);
        initializeWithFilters(product2, null, null, set);

        replay(backlogDAO);
        
        List<BacklogTimesheetNode> result = timesheetBusiness.generateTree(backlogIds, "", "", set);
        
        assertEquals(1, result.size());
        BacklogTimesheetNode current = result.get(0);
        assertEquals("Wrong product,", product2, current.getBacklog());
        
        assertEquals("Wrong number of projects,", 1, current.getChildBacklogs().size());
        current = current.getChildBacklogs().get(0);
        assertEquals(project1, current.getBacklog());
    
        assertEquals("Wrong number of iterations,", 2, current.getChildBacklogs().size());
        for (BacklogTimesheetNode node : current.getChildBacklogs()) {
            assertTrue("Invalid children", project1.getChildren().contains(node.getBacklog()));
        }
    }
    
    /**
     * Checks one generated tree node for treeGeneration2.
     */
    private void checkTree2(List<BacklogTimesheetNode> result, int rootId) {
        BacklogTimesheetNode current = result.get(rootId);
        if (current.getBacklog() == product1) {
            assertEquals("Wrong number of child backlogs for project1.", 0, current.getChildBacklogs().size());
        } else if (current.getBacklog() == product2) { 
            assertEquals("Wrong number of child backlogs for project2.", 1, current.getChildBacklogs().size());
            current = current.getChildBacklogs().get(0);
            assertEquals("Product2's child backlog wasn't project1", project1, current.getBacklog());
            assertEquals("Project1 should only have 1 child backlog.", 1, current.getChildBacklogs().size());
            current = current.getChildBacklogs().get(0);
            assertEquals("Project1's only child backlog wasn't Iteration3.", iteration3, current.getBacklog());
        } else {
            fail("Invalid root node " + rootId);
        }
    }
    
    /**
     * TreeGeneration test for testCompareTrees2.
     */
    private void treeGeneration2(int id1, int id2) {
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(id1);
        backlogIds.add(id2);
        HashSet<Integer> set = new HashSet<Integer>();
        
        initializeWithFilters(backlogs.get(id1), null, null, set);
        initializeWithFilters(backlogs.get(id2), null, null, set);
        
        replay(backlogDAO);
        
        List<BacklogTimesheetNode> result = timesheetBusiness.generateTree(backlogIds, "", "", set);
        
        assertEquals(2, result.size());
        
        if (result.get(0) == result.get(1)) {
            fail("Duplicate backlogs.");
        }
        
        checkTree2(result, 0);
        checkTree2(result, 1);
    }
    
    public void testSums_roots(){
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user1.getId());
        userIds.add(user2.getId());
        
        initializeWithFilters(product1, null, null, userIds);
        initializeWithFilters(product2, null, null, userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
                
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals("Invalid hour total for product1,", 0, product1Node.getHourTotal().getTime());
        assertEquals("Invalid hour total for product2,", 491460, product2Node.getHourTotal().getTime());
        assertEquals("Invalid root sum", 491460, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }
       

    public void testSums_user1Filter() {

        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user1.getId());
        initializeWithFilters(product1, null, null, userIds);
        initializeWithFilters(product2, null, null, userIds);
        replay(backlogDAO);
        replay(hourEntryBusiness);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(326700, product2Node.getHourTotal().getTime());
        assertEquals(326700, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }
    
    public void testSums_user2Filter() {
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user2.getId());
        
        initializeWithFilters(product1, null, null, userIds);
        initializeWithFilters(product2, null, null, userIds);
        
        replay(backlogDAO);
        replay(hourEntryBusiness);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(164760, product2Node.getHourTotal().getTime());
        assertEquals(164760, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }

    public void testSums_timeFilter() {

        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user1.getId());
        userIds.add(user2.getId());
        
        initializeWithFiltersAndParse(product1, "1900-01-01 10:00", "2006-12-12 10:00", userIds);
        initializeWithFiltersAndParse(product2, "1900-01-01 10:00", "2006-12-12 10:00", userIds);
        
        replay(backlogDAO);
        replay(hourEntryBusiness);
        
        roots = timesheetBusiness.generateTree(backlogIds, "1900-01-01 10:00", "2006-12-12 10:00", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(1020, product2Node.getHourTotal().getTime());
        assertEquals(1020, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }
    
    public void testSums_User1TimeFilter() {
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user1.getId());
        
        initializeWithFiltersAndParse(product1, "2007-01-01 12:59", "2007-01-01 13:01", userIds);
        initializeWithFiltersAndParse(product2, "2007-01-01 12:59", "2007-01-01 13:01", userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "2007-01-01 12:59", "2007-01-01 13:01", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(0, product2Node.getHourTotal().getTime());
        assertEquals(0, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }

    public void testSums_NoUserTimeFilter() {
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        
        initializeWithFiltersAndParse(product1, "2008-06-05 12:59", "2008-06-10 13:01", userIds);
        initializeWithFiltersAndParse(product2, "2008-06-05 12:59", "2008-06-10 13:01", userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "2008-06-05 12:59", "2008-06-10 13:01", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(11520, product2Node.getHourTotal().getTime());
        assertEquals(11520, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }

    public void testSums_UsersTimeFilter() {
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user1.getId());
        userIds.add(user2.getId());
                
        initializeWithFiltersAndParse(product1, "2008-06-12 12:59", "2008-06-13 13:01", userIds);
        initializeWithFiltersAndParse(product2, "2008-06-12 12:59", "2008-06-13 13:01", userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "2008-06-12 12:59", "2008-06-13 13:01", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(92160, product2Node.getHourTotal().getTime());
        assertEquals(92160, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }
    
    public void testSums_User3Filter() {
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(product1.getId());
        backlogIds.add(product2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user3.getId());
        
        initializeWithFilters(product1, null, null, userIds);
        initializeWithFilters(product2, null, null, userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        BacklogTimesheetNode product1Node = roots.get(0);
        BacklogTimesheetNode product2Node = roots.get(1);
        
        assertEquals(0, product1Node.getHourTotal().getTime());
        assertEquals(0, product2Node.getHourTotal().getTime());
        assertEquals(0, timesheetBusiness.calculateRootSum(roots).getTime());
        
        // verify(backlogDAO); Does not get all backlogs through backlogDAO, verify should fail
        verify(hourEntryBusiness);
    }
    
    public void testCompareTrees1() {
        replay(hourEntryBusiness);
        treeGeneration1(iteration1.getId(), project1.getId());
    }
    
    public void testCompareTrees2() {
        replay(hourEntryBusiness);
        treeGeneration2(product1.getId(), iteration3.getId());
    }
    
    public void testCompareTrees3() {
        replay(hourEntryBusiness);
        treeGeneration2(iteration3.getId(), product1.getId());
    }
    public void testCompareTrees4() {
        replay(hourEntryBusiness);
        treeGeneration1(project1.getId(), iteration1.getId());
    }
   
    public void testGetHourTotal_Project1(){
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(project1.getId());
        Set<Integer> userIds = new HashSet<Integer>();

        initializeWithFilters(project1, null, null, userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        assertEquals("Invalid sum for product2,", 900, roots.get(0).getHourTotal().getTime());
        
        BacklogTimesheetNode node = roots.get(0).getChildBacklogs().get(0);
        assertEquals("Invalid sum for project1", 900, node.getHourTotal().getTime());
    }
  
    public void testGetHoursForChildBacklogs(){
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(project1.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        
        initializeWithFilters(project1, null, null, userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        BacklogTimesheetNode project1Node = roots.get(0).getChildBacklogs().get(0);
        
        assertEquals("Invalid sum for project1's child backlogs", 900, project1Node.getHoursForChildBacklogs().getTime());
    }
   
    public void testGetHoursForChildBacklogItems(){
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(project1.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        initializeWithFilters(project1, null, null, userIds);
        
        replay(backlogDAO);
        
        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        BacklogTimesheetNode iteration1Node = roots.get(0).getChildBacklogs().get(0).getChildBacklogs().get(0);
        
        assertEquals("Invalid sum for iteration1's child backlog items", 180, iteration1Node.getHoursForChildBacklogItems().getTime());
    }

    public void testGetSpentHours(){
        replay(hourEntryBusiness);
        
        List<BacklogTimesheetNode> roots;
        Set<Integer> backlogIds = new HashSet<Integer>();
        backlogIds.add(project2.getId());
        Set<Integer> userIds = new HashSet<Integer>();
        
        initializeWithFilters(project2, null, null, userIds);
        
        replay(backlogDAO);

        roots = timesheetBusiness.generateTree(backlogIds, "", "", userIds);
        
        BacklogTimesheetNode project2Node = roots.get(0).getChildBacklogs().get(0); 
        
        assertEquals("Invalid sum for project2's hour entries", 369600, project2Node.getSpentHours().getTime());
    }

    /**
     * Inserts all BacklogHourEntries to project2.
     */
    private void insertAllBacklogHourEntries() {
        ArrayList<BacklogHourEntry> entries = new ArrayList<BacklogHourEntry>();
        insertBacklogHourEntry(heList.get(11), project2);
        entries.add((BacklogHourEntry) heList.get(11));
        insertBacklogHourEntry(heList.get(12), project2);
        entries.add((BacklogHourEntry) heList.get(12));
        insertBacklogHourEntry(heList.get(13), project2);
        entries.add((BacklogHourEntry) heList.get(13));
        expect(hourEntryBusiness.getEntriesByParent(project2)).andReturn(entries).anyTimes();
        expect(hourEntryBusiness.getEntriesByParent(product1)).andReturn(null).anyTimes();
        expect(hourEntryBusiness.getEntriesByParent(product2)).andReturn(null).anyTimes();
        expect(hourEntryBusiness.getEntriesByParent(project1)).andReturn(null).anyTimes();
        expect(hourEntryBusiness.getEntriesByParent(iteration1)).andReturn(null).anyTimes();
        expect(hourEntryBusiness.getEntriesByParent(iteration2)).andReturn(null).anyTimes();
        expect(hourEntryBusiness.getEntriesByParent(iteration3)).andReturn(null).anyTimes();
    }
    
    /**
     * Inserts all BacklogItemHourEntries to BLIs.
     */
    private void insertAllBacklogItemHourEntries() {
        bliList.get(0).getHourEntries().add((BacklogItemHourEntry)heList.get(0));
        bliList.get(1).getHourEntries().add((BacklogItemHourEntry)heList.get(1));
        bliList.get(3).getHourEntries().add((BacklogItemHourEntry)heList.get(2));
        bliList.get(3).getHourEntries().add((BacklogItemHourEntry)heList.get(3));
        bliList.get(3).getHourEntries().add((BacklogItemHourEntry)heList.get(4));
        bliList.get(4).getHourEntries().add((BacklogItemHourEntry)heList.get(5));
        bliList.get(4).getHourEntries().add((BacklogItemHourEntry)heList.get(6));
        bliList.get(4).getHourEntries().add((BacklogItemHourEntry)heList.get(7));
        bliList.get(7).getHourEntries().add((BacklogItemHourEntry)heList.get(8));
        bliList.get(7).getHourEntries().add((BacklogItemHourEntry)heList.get(9));
        bliList.get(8).getHourEntries().add((BacklogItemHourEntry)heList.get(10));
    }
    
    /**
     * Inserts the BacklogHourEntry to the specified backlog.
     */
    private void insertBacklogHourEntry(HourEntry he, Backlog backlog) {
        BacklogHourEntry blhe = (BacklogHourEntry) he;
        blhe.setBacklog(backlog);
    }
    
    /**
     * Creates a new product with the given id.
     */
    private Product setUpProduct(int id) {
        Product prod = new Product();
        prod.setId(id);
        expect(backlogDAO.get(id)).andReturn(prod).atLeastOnce();
        return prod;
    }
    
    /**
     * Inserts all the created BLIs into backlogs.
     */
    private void insertAllBacklogItems() {
        insertBliIntoBacklog(bliList.get(0), iteration1);
        insertBliIntoBacklog(bliList.get(1), iteration1);
        insertBliIntoBacklog(bliList.get(2), iteration1);
        insertBliIntoBacklog(bliList.get(3), iteration2);
        insertBliIntoBacklog(bliList.get(4), product2);
        insertBliIntoBacklog(bliList.get(5), product2);
        insertBliIntoBacklog(bliList.get(6), project2);
        insertBliIntoBacklog(bliList.get(7), project2);
        insertBliIntoBacklog(bliList.get(8), project2);
    }
    
    /**
     * Joins given BLI to given backlog.
     */
    private void insertBliIntoBacklog(BacklogItem bli, Backlog backlog) {
        bli.setBacklog(backlog);
        backlog.getBacklogItems().add(bli);
    }
    
    /**
     * Creates a new project with the given id and links it to a product.
     */
    private Project setUpProject(Product prod, int id) {
        Project proj = new Project();
        proj.setId(id);
        proj.setProduct(prod);
        prod.getProjects().add(proj);
        expect(backlogDAO.get(id)).andReturn(proj).atLeastOnce();
        return proj;
    }
    
    /**
     * Creates a new iteration with the given id and links it to a project.
     */
    private Iteration setUpIteration(Project proj, int id) {
        Iteration iter = new Iteration();
        iter.setId(id);
        iter.setProject(proj);
        proj.getIterations().add(iter);
        expect(backlogDAO.get(id)).andReturn(iter).atLeastOnce();
        return iter;
    }
    
    /**
     * Creates all the needed HourEntries, gives them ids and inserts
     * them to heList.
     */
    private void createHourEntries() {
        heList = new ArrayList<HourEntry>();
        for (int i = 1; i <= 11; i++) {
            HourEntry he = new BacklogItemHourEntry();
            he.setId(i);
            heList.add(he);
        }
        
        for (int i = 12; i <= 14; i++) {
            HourEntry he = new BacklogHourEntry();
            he.setId(i);
            heList.add(he);
        }
        insertAllBacklogItemHourEntries();
        insertAllBacklogHourEntries();
        fillAllHourEntries();
    }
    
    /**
     * Fills all the hour entries with data.
     */
    private void fillAllHourEntries() {
        fillHourEntry(heList.get(0),  "1-1-2006 13:00",   60,     user1);
        fillHourEntry(heList.get(1),  "1-1-2007 13:00",   120,    user2);
        fillHourEntry(heList.get(2),  "1-1-2008 13:00",   240,    user1);
        fillHourEntry(heList.get(3),  "10-5-2008 13:00",  480,    user2);
        fillHourEntry(heList.get(4),  "20-5-2008 13:00",  0,      user1);
        fillHourEntry(heList.get(5),  "1-6-2008 13:00",   1920,   user2);
        fillHourEntry(heList.get(6),  "5-6-2008 13:00",   3840,   user1);
        fillHourEntry(heList.get(7),  "10-6-2008 13:00",  7680,   user2);
        fillHourEntry(heList.get(8),  "11-6-2008 13:00",  15360,  user1);
        fillHourEntry(heList.get(9),  "12-6-2008 13:00",  30720,  user2);
        fillHourEntry(heList.get(10), "13-6-2008 13:00",  61440,  user1);
        fillHourEntry(heList.get(11), "10-11-2008 13:00", 122880, user2);
        fillHourEntry(heList.get(12), "1-1-2050 13:00",   245760, user1);
        fillHourEntry(heList.get(13), "5-3-1980 13:00",   960,    user2);
    }
    
    /**
     * Sets the hour entry's date, time and user.
     */
    private void fillHourEntry(HourEntry he, String date, int time, User user) {
        he.setTimeSpent(new AFTime(time));
        he.setUser(user);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        df.setLenient(true);
        try {
            he.setDate(df.parse(date));
        } catch (ParseException pe) {
            
        }
    }
    
    private void createUsers() {
        user1 = new User();
        user1.setId(1);
        user2 = new User();
        user2.setId(2);
        user3 = new User();
        user3.setId(3);
    }
    
    /**
     * Creates all needed BacklogItems, gives them ids and inserts
     * them to bliList.
     */
    private void createBacklogItems() {
        bliList = new ArrayList<BacklogItem>();
        for (int i = 1; i <= 9; i++) {
            BacklogItem bli = new BacklogItem();
            bli.setId(i);
            bliList.add(bli);
        }
        insertAllBacklogItems();
    }
    
    private void initializeBacklog(Backlog backlog, Date start, Date end, Set<Integer> users) {
        
        
        Collection<BacklogItem> returnEntries = new ArrayList<BacklogItem>();
        for(BacklogItem item : backlog.getBacklogItems()) {
            boolean ok = false;
            for(HourEntry he : item.getHourEntries()) {
                boolean accept = true;
                if(start != null && he.getDate().before(start)) {
                    accept = false;
                }
                if(end != null && he.getDate().after(end)) {
                    accept = false;
                }
                if(users != null && users.size() > 0 && !users.contains(he.getUser().getId())) {
                    accept = false;
                }
                if(accept) {
                    ok = true;
                }
            }
            if(ok) {
                returnEntries.add(item);
            } 
        }
        //STUPID "easy"MOCK
        if(backlog instanceof Product) {
            expect(backlogDAO.getBlisWithSpentEffortByBacklog((Product)backlog, start, end, users)).andReturn(returnEntries).anyTimes();
        } else if(backlog instanceof Project) {
            expect(backlogDAO.getBlisWithSpentEffortByBacklog((Project)backlog, start, end, users)).andReturn(returnEntries).anyTimes();
        } else if(backlog instanceof Iteration) {
            expect(backlogDAO.getBlisWithSpentEffortByBacklog((Iteration)backlog, start, end, users)).andReturn(returnEntries).anyTimes();
        }

    }
    private void initializeWithFiltersAndParse(Backlog root, String start, String end, Set<Integer> users) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setLenient(true);
        try {
            Date dstart = df.parse(start);
            Date dend = df.parse(end);
            initializeWithFilters(root, dstart, dend,users);
        } catch (Exception e) {
            fail();
        }
    }
    
    private void initializeWithFilters(Backlog root, Date start, Date end, Set<Integer> users) {
        if(root instanceof Product) {
            for(Project proj : ((Product)root).getProjects()) {
                initializeWithFilters(proj, start, end, users);
            }
        } else if(root instanceof Project) {
            for(Iteration iter: ((Project)root).getIterations()) {
                initializeWithFilters(iter, start, end, users);
            }
        } 
        initializeBacklog(root, start, end, users);
    }
}
