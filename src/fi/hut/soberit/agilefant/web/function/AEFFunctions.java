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
 * custom functions for jsp pages
 */
public class AEFFunctions {

	public static final int MAX_STR_LENGTH = 10;
	public static final int MAX_TITLE_LENGTH = 25;

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
	/**
	 * Chop strings to MAX_STR_LENGTH
	 *  
	 * @param s string to shorten
	 * @return shorter string, or original string if length < MAX_STR_LENGTH
	 */
	public static String out(String s) {
		return out(s, MAX_STR_LENGTH, false); 	
	} 
	/**
	 * Chop strings to MAX_TITLE_LENGTH
	 *  
	 * @param s string to shorten
	 * @return shorter string, or original string if length < MAX_TITLE_LENGTH
	 */
	public static String outTitle(String s) {
		return out(s, MAX_TITLE_LENGTH, false);
	}
	/**
	 * Shorten strings to specified length
	 * 
	 * @param s string to shorten
	 * @param newLength length
	 * @return shorter string, or original string if length < newLength 
	 */
	public static String out(String s, int newLength) {
		return out(s, newLength, false); 	
	} 
	public static String htmlOut(String s) {
		return out(s, MAX_STR_LENGTH, true); 	
	} 
	public static String htmlOut(String s, int newLength) {
		return out(s, newLength, true); 	
	} 
	private static String out(String s, int newLength, boolean asHtml) {
		String shortString = s.length() > newLength ? s.substring(0, newLength) + "..." : s;
		return asHtml ? "<div title=\"" + s + "\">" + shortString + "</div>" : shortString;
	} 
	public static int percent(int amount, int total) {
		return Math.round(total / amount * 100); 	
	} 



}
