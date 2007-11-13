package fi.hut.soberit.agilefant.model;

import junit.framework.TestCase;

/**
 * JUnit test case class for testing BacklogItem.
 * 
 * @author rjokelai
 */
public class BacklogItemTest extends TestCase {

	/**
	 * Test the getParentBacklogs method of class Backlog Item.
	 */
	public void testGetParentBacklogs() {
		Product prod = new Product();
		Deliverable proj = new Deliverable();
		Iteration iter = new Iteration();
		Backlog fake = new Product();

		// Set the parents
		iter.setDeliverable(proj);
		proj.setProduct(prod);

		BacklogItem bli1 = new BacklogItem();
		bli1.setBacklog(iter);

		BacklogItem bli2 = new BacklogItem();
		bli2.setBacklog(proj);

		BacklogItem bli3 = new BacklogItem();
		bli3.setBacklog(prod);

		// Test bli1
		assertTrue(bli1.getParentBacklogs().contains(prod));
		assertTrue(bli1.getParentBacklogs().contains(proj));
		assertTrue(bli1.getParentBacklogs().contains(iter));
		assertFalse(bli1.getParentBacklogs().contains(fake));

		// Test bli2
		assertTrue(bli2.getParentBacklogs().contains(prod));
		assertTrue(bli2.getParentBacklogs().contains(proj));
		assertFalse(bli2.getParentBacklogs().contains(iter));
		assertFalse(bli2.getParentBacklogs().contains(fake));

		// Test bli2
		assertTrue(bli3.getParentBacklogs().contains(prod));
		assertFalse(bli3.getParentBacklogs().contains(proj));
		assertFalse(bli3.getParentBacklogs().contains(iter));
		assertFalse(bli3.getParentBacklogs().contains(fake));
	}
}