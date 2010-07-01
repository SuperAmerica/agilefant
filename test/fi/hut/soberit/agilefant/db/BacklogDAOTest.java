package fi.hut.soberit.agilefant.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class BacklogDAOTest extends AbstractHibernateTests {
    
    @Autowired    
    private BacklogDAO backlogDAO;
    
    @Test
    public void testCalculateStoryPointSum() {
        executeClassSql();
        assertEquals(20, backlogDAO.calculateStoryPointSum(1));
    }
    
    @Test
    public void testCalculateStoryDonePointSum() {
        executeClassSql();
        assertEquals(10, backlogDAO.calculateDoneStoryPointSum(1));
    }

    @Test
    public void testCalculateStoryPointSumIncludeChildBacklogs_iteration() {
        executeClassSql();
        // Sums: 12 + 17
        assertEquals(12+17, backlogDAO.calculateStoryPointSumIncludeChildBacklogs(4));
    }
    
    @Test
    public void testCalculateStoryPointSumIncludeChildBacklogs_project() {
        executeClassSql();
        // Sums: Proj (30), I1 (12+17), I2 (9)
        assertEquals(30+12+17+9, backlogDAO.calculateStoryPointSumIncludeChildBacklogs(3));
    }
    
    @Test
    public void testSearchByName() {
        String search  = "Iteration";
        executeClassSql();
        List<Backlog> backlogs = backlogDAO.searchByName(search);
        assertEquals(4, backlogs.size());
    }

    @Test
    public void testSearchByName_notFound() {
        String search  = "not found string";
        executeClassSql();
        List<Backlog> backlogs = backlogDAO.searchByName(search);
        assertEquals(0, backlogs.size());
    }
    
    @Test
    public void testSearchByName_iterations() {
        String search  = "Iteration";
        executeClassSql();
        List<Backlog> backlogs = backlogDAO.searchByName(search, Iteration.class);
        assertEquals(4, backlogs.size());
    }
    
    @Test
    public void testSearchByName_projects() {
        String search  = "Iteration";
        executeClassSql();
        List<Backlog> backlogs = backlogDAO.searchByName(search, Project.class);
        assertEquals(0, backlogs.size());
    }
}
