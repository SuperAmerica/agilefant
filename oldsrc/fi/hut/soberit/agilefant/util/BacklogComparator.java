package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;

/**
 * A comparator class for sorting Backlogs. When Backlogs of different types are
 * compared Products come first and Projects come before Iterations. When
 * comparing Backlogs of same type, Products are sorted by their id, Projects
 * are sorted by their rank (one with the smaller rank value comes first) and
 * Iterations are sorted by the ranks of their corresponding parent Projects. If
 * the ranks are equal, the sorting is done based on the Backlog's id.
 * 
 * @author mjniemi
 */
public class BacklogComparator implements Comparator<Backlog> {

    /**
     * A method that compares two backlogs and puts them in correct order.
     * 
     * @param o1
     *                first backlog to be compared
     * @param o2
     *                second backlog to be compared
     * @return negative if o1 should go first, positive if o2 should go first, 0
     *         otherwise
     */
    public int compare(Backlog o1, Backlog o2) {
        // comparing Products?
        if (o1 instanceof Product) {
            if (o2 instanceof Product) {
                return compareIds(o1.getId(), o2.getId());
            } else
                return -1;
        } else if (o2 instanceof Product) {
            return 1;
        }

        // comparing Projects?
        if (o1 instanceof Project) {
            if (o2 instanceof Project) {
                int result = compareRanks(((Project) o1).getRank(),
                        ((Project) o2).getRank());
                if (result != 0) {
                    return result;
                } else
                    return compareIds(o1.getId(), o2.getId());
            } else
                return -1;
        } else if (o2 instanceof Project) {
            return 1;
        }

        // comparing Iterations?
        if (o1 instanceof Iteration) {
            if (o2 instanceof Iteration) {
                Project project1 = ((Iteration) o1).getProject();
                Project project2 = ((Iteration) o2).getProject();
                if (project1 == null) {
                    if (project2 == null) {
                        return compareIds(o1.getId(), o2.getId());
                    } else
                        return 1;
                } else if (project2 == null) {
                    return -1;
                } else {
                    int result = compareRanks(project1.getRank(), project2
                            .getRank());
                    if (result != 0) {
                        return result;
                    } else
                        return compareIds(o1.getId(), o2.getId());
                }
            }
        } else if (o2 instanceof Iteration) {
            return 1;
        }

        // only reached when comparing backlogs of unexpected type
        return 0;
    }

    private int compareRanks(int rank1, int rank2) {
        /* If ranks are not set */
        if (rank1 == 0) {
            if (rank2 == 0) {
                return 0;
            } else {
                return 1;
            }
        } else if (rank2 == 0) {
            return -1;
        }

        /* Compare ranks */
        if (rank1 < rank2) {
            return -1;
        } else if (rank1 > rank2) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareIds(int id1, int id2) {
        if (id1 < id2) {
            return -1;
        } else if (id1 > id2) {
            return 1;
        } else
            return 0;
    }
}
