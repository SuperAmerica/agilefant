package fi.hut.soberit.agilefant.business;

import java.util.Date;

import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

public class ProjectBusinessTest extends SpringTestCase {

    private TestUtility testUtility;

    private ProjectBusiness projectBusiness;

    private ProjectDAO projectDAO;

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

        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        // Testing with random ranks.
        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        del1.setRank(new java.util.Random().nextInt(64));
        del2.setRank(new java.util.Random().nextInt(64));
        del3.setRank(new java.util.Random().nextInt(64));
        del4.setRank(new java.util.Random().nextInt(64));
        projectDAO.store(del1);
        projectDAO.store(del2);
        projectDAO.store(del3);
        projectDAO.store(del4);

        projectBusiness.moveToTop(delId1);
        projectBusiness.moveToTop(delId2);
        projectBusiness.moveToTop(delId3);
        projectBusiness.moveToTop(delId4);

        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        del1 = projectDAO.get(delId1);
        del2 = projectDAO.get(delId2);
        del3 = projectDAO.get(delId3);
        del4 = projectDAO.get(delId4);
        assertEquals(4, del1.getRank());
        assertEquals(3, del2.getRank());
        assertEquals(2, del3.getRank());
        assertEquals(1, del4.getRank());
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
