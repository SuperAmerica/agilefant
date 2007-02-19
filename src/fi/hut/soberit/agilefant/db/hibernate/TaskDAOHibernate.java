package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskStatus;

/**
 * Hibernate implementation of TaskDAO interface using GenericDAOHibernate.
 */
public class TaskDAOHibernate extends GenericDAOHibernate<Task> implements TaskDAO {

	public TaskDAOHibernate(){
		super(Task.class);
	}
	
	private String getStatusClause(TaskStatus[] allowedStatuses, String[] ids, Object[] values, int startIndex) {
		String query = "";
		
		boolean prev = false;
		int i = 0;
		for(TaskStatus status : allowedStatuses) {
			if(prev) query += " or ";
			query += "( t.status = :status" + i + " )";  
			ids[i + startIndex] = "status" + i;
			values[i + startIndex] = status;
			i++;  
		}
		
		return query;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<Task> getTasksByStatusAndBacklogItem(BacklogItem bli, TaskStatus[] statuses) {
		String[] ids = new String[statuses.length + 1];
		Object[] values = new Object[statuses.length + 1];
		
		ids[0] = "bliid";
		values[0] = bli.getId();
		
		String query = "from Task t where t.backlogItem.id = :bliid";
		
		if(statuses != null && statuses.length != 0) {
			query += " and ( ";			
			query += getStatusClause(statuses, ids, values, 1);			
			query += " )";
		}
		
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				query,
				ids, 
				values);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<Task> getTasksByStatus(TaskStatus[] statuses) {
		String[] ids = new String[statuses.length];
		Object[] values = new Object[statuses.length];
		
		String query = "from Task t";

		if(statuses != null && statuses.length != 0) {
			query += " where ( ";			
			query += getStatusClause(statuses, ids, values, 0);			
			query += " )";
		}
		
		query += " )";
		
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				query,
				ids, 
				values);
	}		
}
