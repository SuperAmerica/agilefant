package fi.hut.soberit.agilefant.web.function;

import fi.hut.soberit.agilefant.model.AssignEvent;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskComment;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.web.page.ManagementPageItem;
import fi.hut.soberit.agilefant.web.page.PortfolioPageItem;

/**
 * TODO comments jmrantal -  Where are these needed/called
 */
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
	public static boolean isPortfolio(Object obj) {
		return obj instanceof PortfolioPageItem;
	} 
	public static boolean isManagementPage(Object obj) {
		return obj instanceof ManagementPageItem;
	} 
	public static boolean isAssignEvent(Object obj) {
		return obj instanceof AssignEvent;
	} 
	public static boolean isTaskComment(Object obj) {
		return obj instanceof TaskComment;
	} 
	public static boolean isEstimateHistoryEvent(Object obj) {
		return obj instanceof EstimateHistoryEvent;
	} 
	public static boolean isPerformedWork(Object obj) {
		return obj instanceof PerformedWork;
	} 
}
