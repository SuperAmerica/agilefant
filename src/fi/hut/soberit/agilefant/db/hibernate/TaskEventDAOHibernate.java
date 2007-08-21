package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskEvent;

/**
 * Hibernate implementation of TaskEventDAO interface using GenericDAOHibernate.
 */
public class TaskEventDAOHibernate extends GenericDAOHibernate<TaskEvent> implements TaskEventDAO {

	private Log logger = LogFactory.getLog(getClass());
	
	public TaskEventDAOHibernate(){
		super(TaskEvent.class);
	}
	
	@SuppressWarnings(value = "unchecked")
	public AFTime getBLIOriginalEstimate(BacklogItem backlogItem, Date date) {
		String[] queryParams = {"backlogItemId"};
		Object[] queryValues = {backlogItem.getId()};
		List<AFTime> resultList;
		Date creationDate;
		HibernateTemplate ht = super.getHibernateTemplate();
		String query2;
		String query3;
		
		/* Check is bli created after or before the given date */
		String query = "select b.placeHolder.created " +
				"from BacklogItem b " +
				"where b.id = :backlogItemId";
		List<Date> creationDateList = ht.findByNamedParam(query, queryParams, 
				queryValues);
		
		if(creationDateList.isEmpty()) {
			creationDate = new Date(0);
		} else {
			creationDate = creationDateList.get(0);
		}

		if(backlogItem.getPlaceHolder() == null) {
			return backlogItem.getEffortEstimate();
		}
		
		if(creationDate.before(date)){
			queryParams = new String[] {"date", "backlogItem"};
			queryValues = new Object[] {date, backlogItem};
			query3 = "from EstimateHistoryEvent e, BacklogItem b " +
					"where e.task = b.placeHolder and " +
					"b = :backlogItem and " +
					"e.created <= :date and " +
					"e.newEstimate != null";
			
			query2 = "select max(e.created) " + query3 + "";
			
			query = "select f.newEstimate from EstimateHistoryEvent f " +
					"where f.created = (" + query2 + ") and " +
					"f.task.backlogItem = :backlogItem and " +
					"f.task = f.task.backlogItem.placeHolder " +
					"order by f.newEstimate desc";
			resultList = (List<AFTime>)
					ht.findByNamedParam(query, queryParams, queryValues);
		}
		else {
			queryParams = new String[] {"backlogItem"};
			queryValues = new Object[] {backlogItem};
			query3 = "from EstimateHistoryEvent e, BacklogItem b " +
					"where e.task = b.placeHolder " +
					"and b = :backlogItem and " + 
					"e.newEstimate != null";
	
			query2 = "select min(e.created) " + query3 + "";
			
			query = "select f.newEstimate from EstimateHistoryEvent f " +
					"where f.created = (" + query2 + ") and " +
					"f.task.backlogItem = :backlogItem and " +
					"f.task = f.task.backlogItem.placeHolder " +
					"order by f.newEstimate desc";
			resultList = (List<AFTime>)
					ht.findByNamedParam(query, queryParams, queryValues);
		}
		
		if(resultList.isEmpty() || resultList.get(0) == null) {
			return null;
		} else {
			return (AFTime)resultList.get(0);
		}
	}
	
	@SuppressWarnings(value = "unchecked")
	public AFTime getTaskSumOrigEst(BacklogItem backlogItem, Date date) {
		Log logger = LogFactory.getLog(getClass());
		long taskSum = 0;
		String placeholderQuery;
		
		/* Retrive creation date from backlog item's placeholder task */
		String[] queryParams;
		Object[] queryValues;
		List<AFTime> resultList;
		List<Task> realTasks;
		Date creationDate;
		HibernateTemplate ht = super.getHibernateTemplate();
		String query2;
		String query3;
		String query;
		
		/* If backlog item has no tasks return null */
		if(backlogItem.getTasks().isEmpty()) {
			return null;
		}
		
		/* Retrive list of tasks excluding placeholder task ("real tasks") */
		queryParams = new String[] {"backlogItem"};
		queryValues = new Object[] {backlogItem};
		
		/* If placeholder is null dont query it */
		if (backlogItem.getPlaceHolder() != null) {
			placeholderQuery = " and t != t.backlogItem.placeHolder";
		} else {
			placeholderQuery = ""; 
		}
		
		query = 
			"from Task t " +
			"where t.backlogItem = :backlogItem" +
			placeholderQuery;
		
		realTasks = (List<Task>) ht.findByNamedParam(
				query, queryParams, queryValues);
		if(realTasks.isEmpty() || realTasks.get(0) == null) {
			return null;
		}
		
		/* Iterate over the tasks and sum the effor estimate */
		for(Task task: realTasks) {
			if(task.getCreated().before(date)){
				queryParams = new String[] {"task", "date"};
				queryValues = new Object[] {task, date};
				query3 = "from EstimateHistoryEvent e " +
						"where e.task = :task and " +
						"e.created <= :date and " +
						"e.newEstimate != null";
				
				query2 = "select max(created) " + query3 + "";
				
				query = "select f.newEstimate from EstimateHistoryEvent f " +
						"where f.created = (" + query2 + ") and " +
						"f.task = :task " +
						"order by f.newEstimate desc";
				resultList = (List<AFTime>)
						ht.findByNamedParam(query, queryParams, queryValues);
			}
			else {
				queryParams = new String[] {"task"};
				queryValues = new Object[] {task};
				query3 = "EstimateHistoryEvent e " +
						"where e.task = :task and " +
						"e.newEstimate != null";
				query2 = "select min(created) from " + query3 + "";
				
				query = "select f.newEstimate from EstimateHistoryEvent f " +
						"where f.created = (" + query2 + ") and " +
						"f.task = :task " +
						"order by f.newEstimate desc";
				resultList = (List<AFTime>) 
					ht.findByNamedParam(query, queryParams, queryValues);
			}
			if(resultList.size() > 0 && resultList.get(0) != null) {
				taskSum += resultList.get(0).getTime();
			} else {
				return null;
			}
		}
		return new AFTime(taskSum);
	}
}
