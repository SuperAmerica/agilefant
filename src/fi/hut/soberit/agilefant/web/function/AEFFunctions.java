package fi.hut.soberit.agilefant.web.function;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public class AEFFunctions {

	public static boolean isProduct(Object obj) {
		return obj instanceof Product;
	} 
	public static boolean isDeliverable(Object obj) {
		return obj instanceof Deliverable;
	} 
	public static boolean isIteration(Object obj) {
		return obj instanceof Iteration;
	} 
	public static boolean isBacklogItem(Object obj) {
		return obj instanceof BacklogItem;
	} 
	public static boolean isTask(Object obj) {
		return obj instanceof Task;
	} 
	public static boolean isUser(Object obj) {
		return obj instanceof User;
	} 
}
