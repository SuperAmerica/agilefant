package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
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
