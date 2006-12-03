package fi.hut.soberit.agilefant.web.function;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.web.PageItem;

public class AEFFunctions {

	private static final long serialVersionUID = -5331405472857969065L;
	private static ApplicationContext applicationContext;
	public static void setPageContext(PageContext pageContext) {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
	}

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
	public static PageItem getProduct(int id) {
		ProductDAO dao = (ProductDAO)applicationContext.getBean("productDAO");
		return dao.get(id);
	} 
	public static PageItem getDeliverable(int id) {
		DeliverableDAO dao = (DeliverableDAO)applicationContext.getBean("deliverableDAO");
		return dao.get(id);
	} 
	public static PageItem getIteration(int id) {
		IterationDAO dao = (IterationDAO)applicationContext.getBean("iterationDAO");
		return dao.get(id);
	} 
	public static PageItem getBacklogItem(int id) {
		BacklogItemDAO dao = (BacklogItemDAO)applicationContext.getBean("backlogItemDAO");
		return dao.get(id);
	} 
	public static PageItem getBacklog(int id) {
		BacklogDAO dao = (BacklogDAO)applicationContext.getBean("backlogDAO");
		return (PageItem)dao.get(id);
	} 
	public static PageItem getTask(int id) {
		TaskDAO dao = (TaskDAO)applicationContext.getBean("taskDAO");
		return dao.get(id);
	} 
}
