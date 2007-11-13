package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * A comparator for comparing priorities of two backlog items.
 * 
 * @author rjokelai
 * 
 */
public class BacklogItemPriorityComparator implements Comparator<BacklogItem> {

	/**
	 * A comparison method for comparing two backlog items' priorities.
	 * 
	 * @param o1
	 *            first backlog item to be compared
	 * @param o2
	 *            second backlog item to be compared
	 * @return -1 if o1's priority is higher than o2's priority, 0 if equal and
	 *         1 otherwise
	 */
	public int compare(BacklogItem o1, BacklogItem o2) {
		/* Check the priority */
		if (o1.getPriority().getOrdinal() > o2.getPriority().getOrdinal()) {
			return -1;
		} else if (o1.getPriority().getOrdinal() < o2.getPriority()
				.getOrdinal()) {
			return 1;
		} else {
			return 0;
		}
	}

}
