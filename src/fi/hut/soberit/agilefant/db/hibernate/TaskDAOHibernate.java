package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;

public class TaskDAOHibernate extends GenericDAOHibernate<Task> implements TaskDAO {

	public TaskDAOHibernate(){
		super(Task.class);
	}
}
