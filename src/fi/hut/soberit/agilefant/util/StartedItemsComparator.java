package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

/**
 * A comparator class for the default sorting of Started Items -view.
 * @author rjokelai
 */
public class StartedItemsComparator implements Comparator<BacklogItem> {

    /** 
     * A method that compares two backlog items and puts
     * them in correct order.
     * @param o1 first backlogitem to be compared
     * @param o2 second backlogitem to be compared
     * @return negative if o1 should go first, positive if o2 should go first, 0 otherwise  
     */
    public int compare(BacklogItem o1, BacklogItem o2) {
        /* Check, if the backlogitems have a project as a parent */
        
        // If both are products, return 0
        if (o1.getBacklog() instanceof Product &&
                o2.getBacklog() instanceof Product) {
            return 0;
        }
        // If only o1's parent is product, return 1
        else if (o1.getBacklog() instanceof Product &&
                !(o2.getBacklog() instanceof Product)) {
            return 1;
        }
        // If only o2's parent is product, return -1
        else if (o2.getBacklog() instanceof Product &&
                !(o1.getBacklog() instanceof Product)) {
            return -1;
        }

        /* Compare project ranks first */
        Project project1 = getProject(o1);
        Project project2 = getProject(o2);
        
        if (project1 == null || project2 == null) {
            return 0;
        }
        
        int rank1 = project1.getRank();
        int rank2 = project2.getRank();

        /* If ranks are not set */
        if (rank1 == 0) {
            if (rank2 == 0) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else if (rank2 == 0) {
            return -1;
        }
        
        /* Compare ranks */
        if (rank1 < rank2) {
            return -1;
        }
        else if (rank1 > rank2) {
            return 1;
        }
        else {
            return 0;
        }
    }
    
    /**
     * Get the backlogItems project.
     * @param item backlog item
     */
    private Project getProject(BacklogItem item) {
        Backlog backlog = item.getBacklog();
        
        if (backlog instanceof Project) {
            return (Project)backlog;
        }
        else if (backlog instanceof Iteration) {
            return ((Iteration)backlog).getProject();
        }
        
        return null;
    }
}