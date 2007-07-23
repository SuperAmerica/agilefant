package fi.hut.soberit.agilefant.db.hibernate;

import java.sql.Date;

import org.springframework.orm.hibernate3.HibernateTemplate;

import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.TaskEvent;

/**
 * Hibernate implementation of TaskEventDAO interface using GenericDAOHibernate.
 */
public class TaskEventDAOHibernate extends GenericDAOHibernate<TaskEvent> implements TaskEventDAO {

	public TaskEventDAOHibernate(){
		super(TaskEvent.class);
	}
	
	public AFTime getBLIOriginalEstimate(BacklogItem backlogItem, Date date) {
		String[] queryParams = {"backlogItem"};
		Object[] queryValues = {backlogItem};
		Date creationDate;
		HibernateTemplate ht = super.getHibernateTemplate();
		String query2;
		String query3;
		String query = "select placeholder.created " +
				"from :backlogItem";
		creationDate = (Date) ht.findByNamedParam(query, queryParams, 
				queryValues);
		
		queryParams = new String[] {"backlogItem", "date"};
		queryValues = new Object[] {backlogItem, date};
		if(creationDate.before(date)){
//			query = "select event.newEstimate " +
//					"from EstimateHistoryEvent event " +
//					"where max(event.created) in (" +
//						"select event " +
//						"from EstimateHistory event " +
//						"where event.task.backlogItem.backlog.id = :backlogId "+
//						"and event.task = event.task.backlogItem.placeHolder " +
//						"and event.created < :date)";
			query3 = "from EstimateHistoryEvent e" +
					"where e.task = e.task.backlogItem.placeholder and" +
					"e.created < :date";
			
			query2 = "select max(created) from (" + query3 + ")";
			
			query = "select f.newEstimate from EstimateHistoryEvent f" +
					"where f.created = (" + query2 + ")";
		}
		else {
//			query = "select event.newEstimate " +
//					"from EstimateHistoryEvent event " +
//					"where min(event.created) in (" +
//						"select event " +
//						"from EstimateHistory event " +
//						"where event.task.backlogItem.backlog.id = :backlogId "+
//						"and event.task = event.task.backlogItem.placeHolder";
			query3 = "from EstimateHistoryEvent e" +
			"where e.task = e.task.backlogItem.placeholder";
	
			query2 = "select min(created) from (" + query3 + ")";
			
			query = "select f.newEstimate from EstimateHistoryEvent f" +
					"where f.created = (" + query2 + ")";
		}
		return (AFTime) ht.findByNamedParam(query, queryParams, queryValues);
	}
}
