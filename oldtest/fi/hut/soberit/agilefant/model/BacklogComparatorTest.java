package fi.hut.soberit.agilefant.model;

import fi.hut.soberit.agilefant.util.BacklogComparator;
import junit.framework.TestCase;

/**
 * A JUnit test case for testing the BacklogComparator.
 * @author mjniemi
 *
 */
public class BacklogComparatorTest extends TestCase {

    private BacklogComparator comparator = new BacklogComparator();

    /**
     * Method for testing the comparator.
     */
    public void testComparator() {
        /* Create test data */

        Product product0 = new Product();
        
        Product product1 = new Product();

        Project project0 = new Project();
        project0.setRank(1);
        project0.setProduct(product0);
        
        Project project1 = new Project();
        project1.setRank(2);
        project1.setProduct(product1);
        
        Project unrankedProject = new Project();
        unrankedProject.setRank(0);
        unrankedProject.setProduct(product1);

        Iteration iteration0 = new Iteration();
        iteration0.setProject(project0);
        
        Iteration iteration1 = new Iteration();
        iteration1.setProject(project1);
        
        Iteration unrankedIteration = new Iteration();
        unrankedIteration.setProject(unrankedProject);
        
        Iteration noProjectIteration = new Iteration();

        /* The real test */
        
        // Products come before Projects
        assertEquals("Products should come before Projects",
                -1, comparator.compare(product0, project0));
        assertEquals("Products should come before Projects",
                1, comparator.compare(project0, product0));
        
        // Products come before Iterations
        assertEquals("Products should come before Iterations",
                -1, comparator.compare(product1, iteration1));
        assertEquals("Products should come before Iterations",
                1, comparator.compare(iteration1, product1));
        
        // Projects come before Iterations
        assertEquals("Projects should come before Iterations",
                -1, comparator.compare(project0, iteration0));
        assertEquals("Projects should come before Iterations",
                1, comparator.compare(iteration1, project1));
        
        // Project with smaller rank value comes first
        assertEquals("Project with smaller rank value should come first",
                -1, comparator.compare(project0, project1));
        assertEquals("Project with smaller rank value should come first",
                1, comparator.compare(project1, project0));
        
        // Iteration under a higher ranking Project comes first
        assertEquals("Iteration under higher ranking Project should come first",
                -1, comparator.compare(iteration0, iteration1));
        assertEquals("Iteration under higher ranking Project should come first",
                1, comparator.compare(iteration1, iteration0));
        
        // Ranked Project comes before an unranked Project
        assertEquals("Ranked Project should come before an unranked Project",
                -1, comparator.compare(iteration0, unrankedIteration));
        assertEquals("Ranked Project should come before an unranked Project",
                1, comparator.compare(unrankedIteration, iteration1));
        
        // Iteration under a ranked Project comes before one under an unranked Project
        assertEquals("Iteration under ranked Project should come first",
                -1, comparator.compare(iteration0, unrankedIteration));
        assertEquals("Iteration under ranked Project should come first",
                1, comparator.compare(unrankedIteration, iteration1));
        
        // Iteration under a ranked Project comes before one unrelated to a Project
        assertEquals("Iteration under ranked Project should come first",
                -1, comparator.compare(iteration0, noProjectIteration));
        assertEquals("Iteration under ranked Project should come first",
                1, comparator.compare(noProjectIteration, iteration0));
        
        // Backlog compared with itself returns 0
        assertEquals("Backlog compared with itself should return 0",
                0, comparator.compare(product0, product0));
        assertEquals("Backlog compared with itself should return 0",
                0, comparator.compare(project0, project0));
        assertEquals("Backlog compared with itself should return 0",
                0, comparator.compare(iteration0, iteration0));
        assertEquals("Backlog compared with itself should return 0",
                0, comparator.compare(unrankedIteration, unrankedIteration));
        assertEquals("Backlog compared with itself should return 0",
                0, comparator.compare(noProjectIteration, noProjectIteration));
    }
    
}