package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogMetrics;
import fi.hut.soberit.agilefant.util.ProjectMetrics;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

public class ProjectBusinessTest extends SpringTestCase {

    private TestUtility testUtility;

    private ProjectBusiness projectBusiness;

    private ProjectDAO projectDAO;

    @SuppressWarnings("deprecation")
    public void testGetProjectsAndIterationsInTimeFrame() {
        List<Backlog> projects = new ArrayList<Backlog>();
        Project pro = new Project();
        pro.setStartDate(new Date(98, 7, 1));
        pro.setEndDate(new Date(98, 10, 1));

        Iteration it = new Iteration();
        it.setStartDate(new Date(98, 7, 1));
        it.setEndDate(new Date(98, 10, 1));
        System.out.println("Setting project start date:"
                + pro.getStartDate().getDate() + "."
                + pro.getStartDate().getMonth() + "."
                + pro.getStartDate().getYear());
        System.out.println("Setting project start end:"
                + pro.getEndDate().getDate() + "."
                + pro.getEndDate().getMonth() + "."
                + pro.getEndDate().getYear());

        projects.add(pro);
        projects.add(it);

        // 1. Project start,end dates outside time frame
        List<Backlog> list = this.projectBusiness
                .getProjectsAndIterationsInTimeFrame(projects, new Date(98, 8,
                        1), new Date(98, 9, 30));
        assertEquals(2, list.size());

        // 2.Project started, end inside time frame
        projects = new ArrayList<Backlog>();
        pro.setStartDate(new Date(98, 7, 1));
        pro.setEndDate(new Date(98, 9, 1));
        it.setStartDate(new Date(98, 7, 1));
        it.setEndDate(new Date(98, 9, 1));
        projects.add(pro);
        projects.add(it);

        list = this.projectBusiness.getProjectsAndIterationsInTimeFrame(
                projects, new Date(98, 8, 1), new Date(98, 9, 30));
        assertEquals(2, list.size());

        // 3. Project started, end outside time frame
        projects = new ArrayList<Backlog>();
        pro.setStartDate(new Date(98, 8, 15));
        pro.setEndDate(new Date(98, 10, 1));
        projects.add(pro);
        list = this.projectBusiness.getProjectsAndIterationsInTimeFrame(
                projects, new Date(98, 8, 1), new Date(98, 9, 30));
        assertEquals(1, list.size());

        // Project has finished
        list = this.projectBusiness.getProjectsAndIterationsInTimeFrame(
                projects, new Date(2009, 8, 1), new Date(2009, 9, 30));
        assertEquals(0, list.size());

        // Project hasnt started yet
        list = this.projectBusiness.getProjectsAndIterationsInTimeFrame(
                projects, new Date(2006, 11, 1), new Date(2006, 11, 18));
        assertEquals(0, list.size());

    }

    @SuppressWarnings("deprecation")
    public void testCalculateEffortLefts() {
        List<Project> projects = new ArrayList<Project>();
        Project pro = new Project();
        pro.setName("Jorma");
        BacklogItem bli = new BacklogItem();
        bli.setEffortLeft(new AFTime("14h"));
        pro.setStartDate(new Date(98, 9, 5));
        pro.setEndDate(new Date(98, 9, 18));

        ArrayList<BacklogItem> bliList = new ArrayList<BacklogItem>();
        bliList.add(bli);
        pro.setBacklogItems(bliList);
        projects.add(pro);

        HashMap<Backlog, List<BacklogItem>> items = new HashMap<Backlog, List<BacklogItem>>();
        items.put(pro, bliList);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(98, 9, 5));
        int week = cal.get(GregorianCalendar.WEEK_OF_YEAR);

        assertEquals(41, week);
        HashMap<Integer, String> efforts = this.projectBusiness
                .calculateEffortLefts(cal.getTime(), 2, items);
        assertEquals(2, efforts.size());
        assertEquals("7h", efforts.get(new Integer(week)));
        assertEquals("7h", efforts.get(new Integer(week + 1)));
        
       
    }

    @SuppressWarnings("deprecation")
    public void testCalculateEffortLefts_Projects_Responsibles() {
        List<Project> projects = new ArrayList<Project>();
        Project pro = new Project();
        pro.setName("Jorma");
        BacklogItem bli = new BacklogItem();
        bli.setEffortLeft(new AFTime("14h"));
        // Test when assigned responsible -> Should diving by the number
        // of responsibles
        User user = new User();
        user.setLoginName("Esko");
        
        User user2 = new User();
        user2.setLoginName("Teppo");
        List<User> list = new ArrayList<User>();
        list.add(user);
        list.add(user2);
        bli.setResponsibles(list);
        pro.setStartDate(new Date(98, 9, 5));
        pro.setEndDate(new Date(98, 9, 18));

        ArrayList<BacklogItem> bliList = new ArrayList<BacklogItem>();
        bliList.add(bli);
        pro.setBacklogItems(bliList);
        projects.add(pro);

        HashMap<Backlog, List<BacklogItem>> items = new HashMap<Backlog, List<BacklogItem>>();
        items.put(pro, bliList);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(98, 9, 5));
        int week = cal.get(GregorianCalendar.WEEK_OF_YEAR);

        assertEquals(41, week);
        HashMap<Integer, String> efforts = this.projectBusiness
                .calculateEffortLefts(cal.getTime(), 2, items);
        assertEquals(2, efforts.size());
        assertEquals("3h 30min", efforts.get(new Integer(week)));
        assertEquals("3h 30min", efforts.get(new Integer(week + 1)));
        
    }
    @SuppressWarnings("deprecation")
    public void testCalculateEffortLefts_Iterations() {
        List<Iteration> projects = new ArrayList<Iteration>();
        Iteration pro = new Iteration();
        pro.setName("Jorma");
        BacklogItem bli = new BacklogItem();
        bli.setEffortLeft(new AFTime("14h"));
        pro.setStartDate(new Date(98, 9, 5));
        pro.setEndDate(new Date(98, 9, 18));
        ArrayList<BacklogItem> bliList = new ArrayList<BacklogItem>();
        bliList.add(bli);
        pro.setBacklogItems(bliList);
        projects.add(pro);

        HashMap<Backlog, List<BacklogItem>> items = new HashMap<Backlog, List<BacklogItem>>();
        items.put(pro, bliList);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(98, 9, 5));
        int week = cal.get(GregorianCalendar.WEEK_OF_YEAR);

        assertEquals(41, week);
        HashMap<Integer, String> efforts = this.projectBusiness
                .calculateEffortLefts(cal.getTime(), 2, items);
        assertEquals(2, efforts.size());
        assertEquals("7h", efforts.get(new Integer(week)));
        assertEquals("7h", efforts.get(new Integer(week + 1)));
    }

    @SuppressWarnings("deprecation")
    public void testCalculateEffortLefts_Iterations_Responsibles() {
        List<Iteration> projects = new ArrayList<Iteration>();
        Iteration pro = new Iteration();
        pro.setName("Jorma");
        BacklogItem bli = new BacklogItem();
        bli.setEffortLeft(new AFTime("14h"));
        // Test when assigned responsible -> Should diving by the number
        // of responsibles
        User user = new User();
        user.setLoginName("Esko");
        
        User user2 = new User();
        user2.setLoginName("Teppo");
        List<User> list = new ArrayList<User>();
        list.add(user);
        list.add(user2);
        bli.setResponsibles(list);
        pro.setStartDate(new Date(98, 9, 5));
        pro.setEndDate(new Date(98, 9, 18));

        ArrayList<BacklogItem> bliList = new ArrayList<BacklogItem>();
        bliList.add(bli);
        pro.setBacklogItems(bliList);
        projects.add(pro);

        HashMap<Backlog, List<BacklogItem>> items = new HashMap<Backlog, List<BacklogItem>>();
        items.put(pro, bliList);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(98, 9, 5));
        int week = cal.get(GregorianCalendar.WEEK_OF_YEAR);

        assertEquals(41, week);
        HashMap<Integer, String> efforts = this.projectBusiness
                .calculateEffortLefts(cal.getTime(), 2, items);
        assertEquals(2, efforts.size());
        assertEquals("3h 30min", efforts.get(new Integer(week)));
        assertEquals("3h 30min", efforts.get(new Integer(week + 1)));
        
    }

    @SuppressWarnings("deprecation")
    public void testOverheads() {
        BacklogItem bli = new BacklogItem();
        bli.setEffortLeft(new AFTime("6h"));
        User user = new User();
        user.setLoginName("Jorma");

        List<Backlog> projects = new ArrayList<Backlog>();
        Project pro = new Project();
        pro.setName("Project X");
        pro.setStartDate(new Date(98, 9, 1));
        pro.setEndDate(new Date(98, 11, 25));
        pro.setDefaultOverhead(new AFTime("5h"));

        Assignment ass = new Assignment();
        ass.setBacklog(pro);
        ass.setUser(user);
        ass.setDeltaOverhead(new AFTime("5h"));
        List<Assignment> assList = new ArrayList<Assignment>();
        assList.add(ass);
        user.setAssignments(assList);
        pro.setAssignments(assList);

        ArrayList<BacklogItem> bliList = new ArrayList<BacklogItem>();
        bliList.add(bli);
        pro.setBacklogItems(bliList);
        projects.add(pro);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(98, 9, 2));
        int week = cal.get(GregorianCalendar.WEEK_OF_YEAR);

        assertEquals(40, week);
        HashMap<Integer, String> efforts = this.projectBusiness
                .calculateOverheads(cal.getTime(), 2, projects, user);
        assertEquals(2, efforts.size());
        assertEquals("10h", efforts.get(new Integer(week)));
        assertEquals("10h", efforts.get(new Integer(week + 1)));
    }

    /**
     * Tests ProjectBusiness class's methods moveUp, moveDown, moveToTop and
     * moveToBottom.
     */
    public void testProjectBusiness() {
        super.setComplete();

        testUtility.setCleanup(true);

        java.util.Calendar calendar1 = java.util.Calendar.getInstance();
        Date startDate = calendar1.getTime();
        java.util.Calendar calendar2 = java.util.Calendar.getInstance();
        calendar2.setTimeInMillis(calendar1.getTimeInMillis() + 100000000);
        Date endDate = calendar2.getTime();

        // Test data, all projects' ranks are = 0.
        int delId1 = testUtility.createProject("TestProject 1", startDate,
                endDate, null);
        int delId2 = testUtility.createProject("TestProject 2", startDate,
                endDate, null);
        int delId3 = testUtility.createProject("TestProject 3", startDate,
                endDate, null);
        int delId4 = testUtility.createProject("TestProject 4", startDate,
                endDate, null);

        // Move d1 to top and then d2 to top; d1.rank =2, d2.rank = 1.
        projectBusiness.moveToTop(delId1);
        projectBusiness.moveToTop(delId2);
        super.endTransaction();

        super.startNewTransaction();
        super.setComplete();

        Project del1 = projectDAO.get(delId1);
        Project del2 = projectDAO.get(delId2);
        Project del3 = projectDAO.get(delId3);
        Project del4 = projectDAO.get(delId4);
        assertEquals(2, del1.getRank());
        assertEquals(1, del2.getRank());
        assertEquals(0, del3.getRank());
        assertEquals(0, del4.getRank());

        // Move d3 to bottom, then d4 to bottom, then d1 to bottom and then
        // d3 to top;
        // d1.rank = 5, d2.rank = 2, d3.rank = 1, d4.rank = 4,
        projectBusiness.moveToBottom(delId3);
        projectBusiness.moveToBottom(delId4);
        projectBusiness.moveToBottom(delId1);
        projectBusiness.moveToTop(delId3);
        super.endTransaction();

        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(5, del1.getRank());
        assertEquals(2, del2.getRank());
        assertEquals(1, del3.getRank());
        assertEquals(4, del4.getRank());

        // Test moving first ranked to top and last to bottom;
        // there should be no changes.
        projectBusiness.moveToTop(delId3);
        projectBusiness.moveToBottom(delId1);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(5, del1.getRank());
        assertEquals(2, del2.getRank());
        assertEquals(1, del3.getRank());
        assertEquals(4, del4.getRank());

        // Test moving one project up twice and another one once.
        projectBusiness.moveUp(delId4);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(5, del1.getRank());
        assertEquals(3, del2.getRank());
        assertEquals(1, del3.getRank());
        assertEquals(2, del4.getRank());

        projectBusiness.moveUp(delId4);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(5, del1.getRank());
        assertEquals(3, del2.getRank());
        assertEquals(2, del3.getRank());
        assertEquals(1, del4.getRank());

        projectBusiness.moveUp(delId3);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(5, del1.getRank());
        assertEquals(3, del2.getRank());
        assertEquals(1, del3.getRank());
        assertEquals(2, del4.getRank());
        // End testing moving twice and another once.

        // Test moving first project up and last down => no change.
        projectBusiness.moveUp(delId3);
        projectBusiness.moveDown(delId1);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(5, del1.getRank());
        assertEquals(3, del2.getRank());
        assertEquals(1, del3.getRank());
        assertEquals(2, del4.getRank());

        // Test moving one project down three times.
        projectBusiness.moveDown(delId3);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(6, del1.getRank());
        assertEquals(4, del2.getRank());
        assertEquals(3, del3.getRank());
        assertEquals(2, del4.getRank());

        projectBusiness.moveDown(delId3);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(7, del1.getRank());
        assertEquals(4, del2.getRank());
        assertEquals(5, del3.getRank());
        assertEquals(2, del4.getRank());

        projectBusiness.moveDown(delId3);
        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(7, del1.getRank());
        assertEquals(4, del2.getRank());
        assertEquals(8, del3.getRank());
        assertEquals(2, del4.getRank());
    }
    
    /**
     * Test the getAssignableUsers method of project business.
     * <p>
     * The tested method should return a list of all enabled users
     * and users assigned to the project. No duplicates should be
     * in the list.
     */
    public void testGetAssignableUsers() {
        ProjectBusinessImpl projectBiz = new ProjectBusinessImpl();
        BacklogBusiness blogBiz = createMock(BacklogBusiness.class);
        UserBusiness userBiz = createMock(UserBusiness.class);
        
        // Set the mock interfaces
        projectBiz.setBacklogBusiness(blogBiz);
        projectBiz.setUserBusiness(userBiz);
        
        // Create test data
        List<User> enabledList = new ArrayList<User>();
        List<User> assignedList = new ArrayList<User>();
        Project proj = new Project();
        
        // Enabled user assigned to project
        User user1 = new User();
        user1.setId(1);
        user1.setEnabled(true);
        enabledList.add(user1);
        assignedList.add(user1);
        
        // Disabled user assigned to project
        User user2 = new User();
        user2.setId(2);
        user2.setEnabled(false);
        assignedList.add(user2);
                
        // Enabled user not assigned to project
        User user3 = new User();
        user3.setId(3);
        user3.setEnabled(true);
        enabledList.add(user3);
        
        // Disabled user not assigned to project
        User user4 = new User();
        user4.setId(4);
        user4.setEnabled(false);
        
        // Record expected behavior
        expect(blogBiz.getUsers(proj, true)).andReturn(assignedList);
        expect(userBiz.getEnabledUsers()).andReturn(enabledList);
        
        // Test it
        replay(blogBiz);
        replay(userBiz);
        
        List<User> userList = projectBiz.getAssignableUsers(proj);
        
        // Check, that no duplicates exist
        Set<User> userSet = new HashSet<User>();
        for (User user : userList) {
            if (userSet.contains(user)) {
                fail("Duplicates exist");
            }
            else {
                userSet.add(user);
            }
        }
        
        // Check, that correct users are in the list
        assertTrue(userList.contains(user1));
        assertTrue(userList.contains(user2));
        assertTrue(userList.contains(user3));
        assertFalse(userList.contains(user4));
        
        verify(blogBiz);
        verify(userBiz);
    }

    public void testGetProjectMetrics() {
        BacklogBusiness blBus = EasyMock.createMock(BacklogBusiness.class);
        ProjectDAO pDAO = EasyMock.createMock(ProjectDAO.class);
        ProjectBusinessImpl pBus = new ProjectBusinessImpl();
        pBus.setBacklogBusiness(blBus);
        pBus.setProjectDAO(pDAO);
        
        Project emptyProject = new Project();
        ProjectMetrics emptyProjectMetrics = new ProjectMetrics(); //empty
        BacklogMetrics emptyProjectContentMetrics = new BacklogMetrics(); //empty
        
        Project noIterationProject = new Project();
        ProjectMetrics noIterationProjectMetrics = new ProjectMetrics();
        
        noIterationProjectMetrics.setOriginalEstimate(new AFTime(1000));
        noIterationProjectMetrics.setEffortLeft(new AFTime(500));
        noIterationProjectMetrics.setTotalItems(10);
        noIterationProjectMetrics.setCompletedItems(4);
        
        BacklogMetrics noIterationProjectContentMetrics = new BacklogMetrics(); //empty
        
        
        Project allInProject = new Project();
        ProjectMetrics allInProjectMetrics = new ProjectMetrics();
        
        allInProjectMetrics.setOriginalEstimate(new AFTime(12000));
        allInProjectMetrics.setEffortLeft(new AFTime(6000));
        allInProjectMetrics.setTotalItems(10);
        allInProjectMetrics.setCompletedItems(4);
        
        BacklogMetrics allInProjectContentMetrics = new BacklogMetrics();
        
        allInProjectContentMetrics.setOriginalEstimate(new AFTime(60000));
        allInProjectContentMetrics.setEffortLeft(new AFTime(40000));
        allInProjectContentMetrics.setTotalItems(50);
        allInProjectContentMetrics.setCompletedItems(10);
        
        expect(blBus.calculateLimitedBacklogMetrics(emptyProject)).andReturn(emptyProjectContentMetrics);
        expect(blBus.calculateLimitedBacklogMetrics(noIterationProject)).andReturn(noIterationProjectContentMetrics);
        expect(blBus.calculateLimitedBacklogMetrics(allInProject)).andReturn(allInProjectContentMetrics);
        
        expect(pDAO.getProjectBLIMetrics(emptyProject)).andReturn(emptyProjectMetrics);
        //expect(pDAO.getDoneBLIs(emptyProject)).andReturn(0);
        expect(pDAO.getProjectBLIMetrics(noIterationProject)).andReturn(noIterationProjectMetrics);
        expect(pDAO.getDoneBLIs(noIterationProject)).andReturn(4);
        expect(pDAO.getProjectBLIMetrics(allInProject)).andReturn(allInProjectMetrics);
        expect(pDAO.getDoneBLIs(allInProject)).andReturn(4);
        
        replay(pDAO);
        replay(blBus);
        
        ProjectMetrics m1 = pBus.getProjectMetrics(emptyProject);
        assertEquals(0, m1.getEffortLeft().getTime());
        assertEquals(0, m1.getOriginalEstimate().getTime());
        assertEquals(new Integer(0), m1.getTotalItems());
        assertEquals(new Integer(0), m1.getCompletedItems());
        assertEquals(new Integer(0), m1.getPercentDone());
        
        ProjectMetrics m2 = pBus.getProjectMetrics(noIterationProject);
        assertEquals(500, m2.getEffortLeft().getTime());
        assertEquals(1000, m2.getOriginalEstimate().getTime());
        assertEquals(new Integer(10), m2.getTotalItems());
        assertEquals(new Integer(4), m2.getCompletedItems());
        assertEquals(new Integer(40), m2.getPercentDone());
        
        ProjectMetrics m3 = pBus.getProjectMetrics(allInProject);
        assertEquals(46000, m3.getEffortLeft().getTime());
        assertEquals(72000, m3.getOriginalEstimate().getTime());
        assertEquals(new Integer(60), m3.getTotalItems());
        assertEquals(new Integer(14), m3.getCompletedItems());
        assertEquals(new Integer(23), m3.getPercentDone());
        
        verify(pDAO);
        verify(blBus);        
    }
    
    public void testFormatThemeBindings() {
        //setup
        ProjectDAO pDAO = EasyMock.createMock(ProjectDAO.class);
        ProjectBusinessImpl pBus = new ProjectBusinessImpl();
        pBus.setProjectDAO(pDAO);
        
        BusinessTheme theme1 = new BusinessTheme();
        BusinessTheme theme2 = new BusinessTheme();
        BusinessTheme theme3 = new BusinessTheme();
        BacklogThemeBinding binding1 = new BacklogThemeBinding();
        binding1.setBusinessTheme(theme1);
        binding1.setFixedSize(new AFTime(10));
        binding1.setRelativeBinding(false);
        BacklogThemeBinding binding2 = new BacklogThemeBinding();
        binding2.setBusinessTheme(theme2);
        binding2.setFixedSize(new AFTime(562));
        binding2.setRelativeBinding(false);
        BacklogThemeBinding binding3 = new BacklogThemeBinding();
        binding3.setBusinessTheme(theme3);
        binding3.setFixedSize(new AFTime(2));
        binding3.setRelativeBinding(false);
        BacklogThemeBinding binding4 = new BacklogThemeBinding();
        binding4.setBusinessTheme(theme1);
        binding4.setFixedSize(new AFTime(150));
        binding4.setRelativeBinding(false);
        BacklogThemeBinding binding5 = new BacklogThemeBinding();
        binding5.setBusinessTheme(theme2);
        binding5.setFixedSize(new AFTime(4000));
        binding5.setRelativeBinding(false);
        
        Iteration mockIter = new Iteration();
        ArrayList<Iteration> emptyIter = new ArrayList<Iteration>();
        ArrayList<Iteration> iterList = new ArrayList<Iteration>();
        iterList.add(mockIter);
        ArrayList<BacklogThemeBinding> emptyBind = new ArrayList<BacklogThemeBinding>();

        
        // case 1 no theme bindings
        Project project1 = new Project();
        project1.setIterations(emptyIter);
        
        // case 2 only iteration bindings
        Project project2 = new Project();
        List<BacklogThemeBinding> iterationThemes2 = new ArrayList<BacklogThemeBinding>();
        iterationThemes2.add(binding1);
        iterationThemes2.add(binding2);
        iterationThemes2.add(binding2);
        project2.setIterations(iterList);
        
        // case 3 only project bindings
        Project project3 = new Project();
        List<BacklogThemeBinding> projectThemes3 = new ArrayList<BacklogThemeBinding>();
        project3.setBusinessThemeBindings(projectThemes3);
        projectThemes3.add(binding1);
        projectThemes3.add(binding1);
        projectThemes3.add(binding5);
        project3.setIterations(emptyIter);

        
        
        // case 4 iteration and project bindings
        Project project4 = new Project();
        List<BacklogThemeBinding> iterationThemes4 = new ArrayList<BacklogThemeBinding>();
        List<BacklogThemeBinding> projectThemes4 = new ArrayList<BacklogThemeBinding>();
        project4.setBusinessThemeBindings(projectThemes4);
        iterationThemes4.add(binding3);
        iterationThemes4.add(binding4);
        iterationThemes4.add(binding4);
        iterationThemes4.add(binding1);
        iterationThemes4.add(binding1);
        iterationThemes4.add(binding1);
        projectThemes4.add(binding3);
        projectThemes4.add(binding1);
        project4.setIterations(iterList);

        expect(pDAO.getProjectThemeData(project2)).andReturn(iterationThemes2);
        expect(pDAO.getProjectThemeData(project4)).andReturn(iterationThemes4);
        
        replay(pDAO);
        Map<BusinessTheme,AFTime> ret;
        ret = pBus.formatThemeBindings(project1);
        assertEquals(0, ret.size());
        
        ret = pBus.formatThemeBindings(project2);
        assertEquals(2, ret.size());
        assertEquals(10, ret.get(theme1).getTime());
        assertEquals(1124, ret.get(theme2).getTime());
        
        ret = pBus.formatThemeBindings(project3);
        assertEquals(2, ret.size());
        assertEquals(20, ret.get(theme1).getTime());
        assertEquals(4000, ret.get(theme2).getTime());
        
        ret = pBus.formatThemeBindings(project4);
        assertEquals(2, ret.size());
        assertEquals(340, ret.get(theme1).getTime());
        assertEquals(4, ret.get(theme3).getTime());
      
        
        verify(pDAO);
        
        
    }
    /**
     * Clears the database from test data. We must clear database manually if we
     * need to transactions to complete.
     */
    protected void onTearDownInTransaction() throws Exception {
        testUtility.clearDBStack();
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public TestUtility getTestUtility() {
        return testUtility;
    }

    public void setTestUtility(TestUtility testUtility) {
        this.testUtility = testUtility;
    }

}
