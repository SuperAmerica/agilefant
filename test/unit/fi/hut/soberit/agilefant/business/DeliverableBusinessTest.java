package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.BacklogValueInjector;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;
import fi.hut.soberit.agilefant.business.DeliverableBusiness;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import java.util.Date;

public class DeliverableBusinessTest extends SpringTestCase {
	
	private TestUtility testUtility;
	private DeliverableBusiness deliverableBusiness;
	private DeliverableDAO deliverableDAO;
	
	/**
	 * Tests DeliverableBusiness class's methods moveUp, moveDown,
	 * moveToTop and moveToBottom.
	 */
	public void testDeliverableBusiness() {
		super.setComplete();
		
		testUtility.setCleanup(true);
		
		java.util.Calendar calendar1 = java.util.Calendar.getInstance();
		Date startDate = calendar1.getTime();
		java.util.Calendar calendar2 = java.util.Calendar.getInstance();
		calendar2.setTimeInMillis(calendar1.getTimeInMillis() + 100000000);
		Date endDate = calendar2.getTime();
		
		// Test data, all projects' ranks are = 0.
		int delId1 = testUtility.createProject("TestDeliverable 1", startDate, endDate, null);
		int delId2 = testUtility.createProject("TestDeliverable 2", startDate, endDate, null);
		int delId3 = testUtility.createProject("TestDeliverable 3", startDate, endDate, null);
		int delId4 = testUtility.createProject("TestDeliverable 4", startDate, endDate, null);
		
		
		
		// Move d1 to top and then d2 to top; d1.rank =2, d2.rank = 1.
		deliverableBusiness.moveToTop(delId1);
		deliverableBusiness.moveToTop(delId2);
		super.endTransaction();
		
		super.startNewTransaction();
		super.setComplete();
		
		Deliverable del1 = deliverableDAO.get(delId1);
		Deliverable del2 = deliverableDAO.get(delId2);
		Deliverable del3 = deliverableDAO.get(delId3);
		Deliverable del4 = deliverableDAO.get(delId4);
		assertEquals(2, del1.getRank());
		assertEquals(1, del2.getRank());
		assertEquals(0, del3.getRank());
		assertEquals(0, del4.getRank());
		
		// Move d3 to bottom, then d4 to bottom, then d1 to bottom and then
		// d3 to top;
		// d1.rank = 5, d2.rank = 2, d3.rank = 1, d4.rank = 4,
		deliverableBusiness.moveToBottom(delId3);
		deliverableBusiness.moveToBottom(delId4);
		deliverableBusiness.moveToBottom(delId1);
		deliverableBusiness.moveToTop(delId3);
		super.endTransaction();
		
		super.startNewTransaction();
		super.setComplete();
			
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(5, del1.getRank());
		assertEquals(2, del2.getRank());
		assertEquals(1, del3.getRank());
		assertEquals(4, del4.getRank());
		
		// Test moving first ranked to top and last to bottom;
		// there should be no changes.
		deliverableBusiness.moveToTop(delId3);
		deliverableBusiness.moveToBottom(delId1);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(5, del1.getRank());
		assertEquals(2, del2.getRank());
		assertEquals(1, del3.getRank());
		assertEquals(4, del4.getRank());
		
		// Test moving one project up twice and another one once.
		deliverableBusiness.moveUp(delId4);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(5, del1.getRank());
		assertEquals(3, del2.getRank());
		assertEquals(1, del3.getRank());
		assertEquals(2, del4.getRank());
		
		deliverableBusiness.moveUp(delId4);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(5, del1.getRank());
		assertEquals(3, del2.getRank());
		assertEquals(2, del3.getRank());
		assertEquals(1, del4.getRank());
		
		deliverableBusiness.moveUp(delId3);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(5, del1.getRank());
		assertEquals(3, del2.getRank());
		assertEquals(1, del3.getRank());
		assertEquals(2, del4.getRank());
		// End testing moving twice and another once.
		
		// Test moving first project up and last down => no change.
		deliverableBusiness.moveUp(delId3);
		deliverableBusiness.moveDown(delId1);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(5, del1.getRank());
		assertEquals(3, del2.getRank());
		assertEquals(1, del3.getRank());
		assertEquals(2, del4.getRank());
		
		// Test moving one project down three times.
		deliverableBusiness.moveDown(delId3);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(6, del1.getRank());
		assertEquals(4, del2.getRank());
		assertEquals(3, del3.getRank());
		assertEquals(2, del4.getRank());
		
		deliverableBusiness.moveDown(delId3);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(7, del1.getRank());
		assertEquals(4, del2.getRank());
		assertEquals(5, del3.getRank());
		assertEquals(2, del4.getRank());
		
		deliverableBusiness.moveDown(delId3);
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(7, del1.getRank());
		assertEquals(4, del2.getRank());
		assertEquals(8, del3.getRank());
		assertEquals(2, del4.getRank());
		
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		// Testing with random ranks.
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		del1.setRank(new java.util.Random().nextInt(64));
		del2.setRank(new java.util.Random().nextInt(64));
		del3.setRank(new java.util.Random().nextInt(64));
		del4.setRank(new java.util.Random().nextInt(64));
		deliverableDAO.store(del1);
		deliverableDAO.store(del2);
		deliverableDAO.store(del3);
		deliverableDAO.store(del4);
		
		deliverableBusiness.moveToTop(delId1);
		deliverableBusiness.moveToTop(delId2);
		deliverableBusiness.moveToTop(delId3);
		deliverableBusiness.moveToTop(delId4);
		
		super.endTransaction();
		super.startNewTransaction();
		super.setComplete();
		
		del1 = deliverableDAO.get(delId1);
		del2 = deliverableDAO.get(delId2);
		del3 = deliverableDAO.get(delId3);
		del4 = deliverableDAO.get(delId4);
		assertEquals(4, del1.getRank());
		assertEquals(3, del2.getRank());
		assertEquals(2, del3.getRank());
		assertEquals(1, del4.getRank());
	}
	
	/**
	 * Clears the database from test data.
	 * We must clear database manually if we need to transactions to complete.
	 */
	protected void onTearDownInTransaction() throws Exception {
		testUtility.clearDBStack();
	}
	
	
	public DeliverableBusiness getDeliverableBusiness() {
		return deliverableBusiness;
	}
	
	public void setDeliverableBusiness(DeliverableBusiness deliverableBusiness) {
		this.deliverableBusiness = deliverableBusiness;
	}
	
	public DeliverableDAO getDeliverableDAO() {
		return deliverableDAO;
	}
	
	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}

	public TestUtility getTestUtility() {
		return testUtility;
	}

	public void setTestUtility(TestUtility testUtility) {
		this.testUtility = testUtility;
	}
	
}
