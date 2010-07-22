package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

import static org.junit.Assert.*;

@ContextConfiguration
@Transactional
public class TaskHourEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired    
    private TaskHourEntryDAO testable;
    
    @Test
    public void retrieveByTask() {
        executeClassSql();
        Task task = new Task();
        task.setId(1);
        List<TaskHourEntry> returned = testable.retrieveByTask(task);
        assertEquals(2, returned.size());
    }
    
    @Test
    public void retrieveByTask_noEntries() {
        executeClassSql();
        Task task = new Task();
        task.setId(7);
        List<TaskHourEntry> returned = testable.retrieveByTask(task);
        assertEquals(0, returned.size());
    }
}
