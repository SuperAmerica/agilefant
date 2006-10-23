package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.TaskEvent;

public class TaskEventDAOHibernate extends GenericDAOHibernate<TaskEvent> implements TaskEventDAO {

	public TaskEventDAOHibernate(){
		super(TaskEvent.class);
	}
}
