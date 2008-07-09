package fi.hut.soberit.agilefant.model;

import fi.hut.soberit.agilefant.util.StartedItemsComparator;
import junit.framework.TestCase;

/**
 * A JUnit test case for testing the startedItemsComparator.
 * @author rjokelai
 *
 */
public class StartedItemsComparatorTest extends TestCase {
    
    private StartedItemsComparator comparator = new StartedItemsComparator();
    
    /**
     * Method for testing the comparator.
     */
    public void testComparator() {
        /* Create test data */
        Product product = new Product();
        
        Project project0 = new Project();
        project0.setRank(0);
        project0.setProduct(product);
        Project project1 = new Project();
        project1.setRank(1);
        project0.setProduct(product);
        Project project2 = new Project();
        project2.setRank(2);
        project0.setProduct(product);
        
        Iteration iter0 = new Iteration();
        iter0.setProject(project0);
        Iteration iter1 = new Iteration();
        iter1.setProject(project1);
        Iteration iter2 = new Iteration();
        iter2.setProject(project2);
        
        BacklogItem[] bliArray = new BacklogItem[9];
        for (int i = 0; i < 9; i++) {
            bliArray[i] = new BacklogItem();
        }
        bliArray[0].setBacklog(product);
        bliArray[1].setBacklog(product);
        bliArray[2].setBacklog(project0);
        bliArray[3].setBacklog(project0);
        bliArray[4].setBacklog(project1);
        bliArray[5].setBacklog(project2);
        bliArray[6].setBacklog(iter0);
        bliArray[7].setBacklog(iter1);
        bliArray[8].setBacklog(iter2);
        
        
        /* The real test */
        
        // Both parent backlogs are products
        assertEquals("Both backlog items are in a product",
                0, comparator.compare(bliArray[0], bliArray[1]));
        // Other BLI is in a project, should be later
        assertEquals("BLI in a project should be later",
                1, comparator.compare(bliArray[0], bliArray[2]));
        
        // Both BLI:s are in the same project
        assertEquals("Both backlog items in the same project",
                0, comparator.compare(bliArray[3], bliArray[2]));
        // Other BLI's project rank is higher
        assertEquals("Backlog item with ranked project should come first",
                -1, comparator.compare(bliArray[4], bliArray[2]));
        
        assertEquals("Backlog item with higher project rank should come first",
                -1, comparator.compare(bliArray[4], bliArray[5]));
        
        // Test with iterations mixed with projects and products
        assertEquals("BLI in iteration should come first",
                1, comparator.compare(bliArray[0], bliArray[6]));
        assertEquals("Both are in same project",
                0, comparator.compare(bliArray[2], bliArray[6]));
        assertEquals("Both are in same project",
                0, comparator.compare(bliArray[4], bliArray[7]));
        assertEquals("BLI with project rank should come first",
                1, comparator.compare(bliArray[3], bliArray[7]));
        assertEquals("BLI with project rank should come first",
                -1, comparator.compare(bliArray[7], bliArray[8]));
    }
    
}