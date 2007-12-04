package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

/**
 * A test case for testing sorted backlogItems list.
 * 
 * @author rjokelai
 */
public class BacklogTest_getSortedBacklogItems extends TestCase {
    private Backlog backlog;

    private Collection<BacklogItem> backlogItems;

    private BacklogItem done_4;

    private BacklogItem done_5;

    private BacklogItem notstarted_4;

    private BacklogItem notstarted_5;

    /**
     * Constructor for the test case. Set the test values.
     */
    public void setUp() {
        this.backlog = new Iteration();
        this.backlogItems = new ArrayList<BacklogItem>();

        /* Create the backlog items */
        this.done_4 = new BacklogItem();
        this.done_4.setPriority(Priority.CRITICAL);
        this.done_4.setState(State.DONE);
        this.done_5 = new BacklogItem();
        this.done_5.setPriority(Priority.BLOCKER);
        this.done_5.setState(State.DONE);
        this.notstarted_4 = new BacklogItem();
        this.notstarted_4.setPriority(Priority.CRITICAL);
        this.notstarted_4.setState(State.NOT_STARTED);
        this.notstarted_5 = new BacklogItem();
        this.notstarted_5.setPriority(Priority.BLOCKER);
        this.notstarted_5.setState(State.NOT_STARTED);
    }

    /**
     * Method for testing the getSortedBacklogItems-method of class Backlog.
     * Tests with all items in the list.
     */
    public void testWithAllItems() {
        /* Add the backlog items to the arraylist */
        this.backlogItems.add(this.notstarted_5);
        this.backlogItems.add(this.notstarted_4);
        this.backlogItems.add(this.done_5);
        this.backlogItems.add(this.done_4);

        /* Set the backlog's backlog items */
        this.backlog.setBacklogItems(this.backlogItems);

        /* Get the sorted list */
        ArrayList<BacklogItem> testlist = (ArrayList<BacklogItem>) this.backlog
                .getSortedBacklogItems();

        /* Check the initial conditions */
        assertTrue("Sorted list does not contain all test data.", testlist
                .containsAll(this.backlogItems));

        assertSame("First item does not match", this.notstarted_5, testlist
                .get(0));
        assertSame("Second item does not match", this.notstarted_4, testlist
                .get(1));
        assertSame("Third item does not match", this.done_5, testlist.get(2));
        assertSame("Fourth item does not match", this.done_4, testlist.get(3));

        /* Shuffle the list */
        Collections.shuffle(testlist);
        this.backlog.setBacklogItems(testlist);

        testlist = (ArrayList<BacklogItem>) this.backlog
                .getSortedBacklogItems();

        assertSame("First item does not match", this.notstarted_5, testlist
                .get(0));
        assertSame("Second item does not match", this.notstarted_4, testlist
                .get(1));
        assertSame("Third item does not match", this.done_5, testlist.get(2));
        assertSame("Fourth item does not match", this.done_4, testlist.get(3));
    }

    /**
     * Method for testing the getSortedBacklogItems-method of class Backlog.
     * Tests with an empty list.
     */
    public void testWithEmptyList() {
        ArrayList<BacklogItem> testlist = new ArrayList<BacklogItem>();

        this.backlog.setBacklogItems(testlist);

        assertTrue("List of backlog items is not empty", this.backlog
                .getSortedBacklogItems().isEmpty());
    }

    /**
     * Method for testing the getSortedBacklogItems-method of class Backlog.
     * Tests with only not started states.
     */
    public void testWithNotStartedOnly() {
        ArrayList<BacklogItem> testlist = new ArrayList<BacklogItem>();

        /* Populate the list */
        testlist.add(this.notstarted_4);
        testlist.add(this.notstarted_5);
        this.backlog.setBacklogItems(testlist);

        testlist = (ArrayList<BacklogItem>) this.backlog
                .getSortedBacklogItems();

        assertSame("First item does not match", this.notstarted_5, testlist
                .get(0));
        assertSame("Second item does not match", this.notstarted_4, testlist
                .get(1));
    }

    /**
     * Method for testing the getSortedBacklogItems-method of class Backlog.
     * Tests with only done states.
     */
    public void testWithDoneOnly() {
        ArrayList<BacklogItem> testlist = new ArrayList<BacklogItem>();

        /* Populate the list */
        testlist.add(this.done_4);
        testlist.add(this.done_5);
        this.backlog.setBacklogItems(testlist);

        testlist = (ArrayList<BacklogItem>) this.backlog
                .getSortedBacklogItems();

        assertSame("First item does not match", this.done_5, testlist.get(0));
        assertSame("Second item does not match", this.done_4, testlist.get(1));
    }
}
