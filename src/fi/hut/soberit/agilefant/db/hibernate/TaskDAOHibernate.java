package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;

@Repository("taskDAO")
public class TaskDAOHibernate extends GenericDAOHibernate<Task> implements TaskDAO {
    
    public TaskDAOHibernate() {
        super(Task.class);
    }
    
}
