package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
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
import junit.framework.TestCase;

public class TimesheetBusinessTest extends TestCase {
    private Product product1, product2;
    private Project project1, project2;
    private Iteration iteration1, iteration2, iteration3;
    private ArrayList<BacklogItem> bliList;
    private ArrayList<HourEntry> heList; 
    private User user1, user2;
    private HourEntryBusiness hourEntryBusiness;
    private UserBusiness userBusiness;
    private UserDAO userDAO;
    private BacklogDAO backlogDAO;
    
    public void setUp() {
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        userBusiness = createMock(UserBusiness.class);
        userDAO = createMock(UserDAO.class);
        backlogDAO = createMock(BacklogDAO.class);
        product1 = setUpProduct(1);
        product2 = setUpProduct(2);
        project1 = setUpProject(product2, 3);
        project2 = setUpProject(product2, 4);
        iteration1 = setUpIteration(project1, 5);
        iteration2 = setUpIteration(project1, 6);
        iteration3 = setUpIteration(project1, 7);
        createUsers();
        createBacklogItems();
        createHourEntries();
    }

    public void testSomething() {
        
    }
    
    /**
     * Inserts all BacklogHourEntries to project2.
     */
    private void insertAllBacklogHourEntries() {
        insertBacklogHourEntry(heList.get(11), project2);
        insertBacklogHourEntry(heList.get(12), project2);
        insertBacklogHourEntry(heList.get(13), project2);
    }
    
    /**
     * Inserts all BacklogItemHourEntries to BLIs.
     */
    private void insertAllBacklogItemHourEntries() {
        insertBacklogItemHourEntry(heList.get(0), bliList.get(0));
        insertBacklogItemHourEntry(heList.get(1), bliList.get(1));
        insertBacklogItemHourEntry(heList.get(2), bliList.get(3));
        insertBacklogItemHourEntry(heList.get(3), bliList.get(3));
        insertBacklogItemHourEntry(heList.get(4), bliList.get(3));
        insertBacklogItemHourEntry(heList.get(5), bliList.get(4));
        insertBacklogItemHourEntry(heList.get(6), bliList.get(4));
        insertBacklogItemHourEntry(heList.get(7), bliList.get(4));
        insertBacklogItemHourEntry(heList.get(8), bliList.get(7));
        insertBacklogItemHourEntry(heList.get(9), bliList.get(7));
        insertBacklogItemHourEntry(heList.get(10), bliList.get(8));
    }
    
    /**
     * Inserts the BacklogItemHourEntry to the specified BLI.
     */
    private void insertBacklogItemHourEntry(HourEntry he, BacklogItem bli) {
        BacklogItemHourEntry blihe = (BacklogItemHourEntry) he;
        blihe.setBacklogItem(bli);
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
        return prod;
    }
    
    /**
     * Inserts all the created BLIs into their specified backlogs.
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
}
