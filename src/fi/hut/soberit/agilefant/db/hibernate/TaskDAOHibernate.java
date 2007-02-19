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
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<Task> getTasksByStatusAndBacklogItem(BacklogItem bli, TaskStatus[] statuses) {
		String[] ids = new String[statuses.length + 1];
		Object[] values = new Object[statuses.length + 1];
		
		ids[0] = "bliid";
		values[0] = bli.getId();
		
		String query = "from Task t where t.backlogItem.id = :bliid and ( ";
		boolean prev = false;
		int i = 0;
		for(TaskStatus status : statuses) {
			if(prev) query += " or ";
			query += "( t.status = :status" + i + " )";  
			ids[i+1] = "status" + i;
			values[i+1] = status;
			i++;  
		}
		
		query += " )";
		
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				query,
				ids, 
				values);
	}	
}
