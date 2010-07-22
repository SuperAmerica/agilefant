package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

import static org.junit.Assert.*;

@ContextConfiguration
@Transactional
public class BacklogHourEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired    
    private BacklogHourEntryDAO testable;
    
    @Test
    public void retrieveByBacklog() {
        executeClassSql();
        Project project = new Project();
        project.setId(3);
        List<BacklogHourEntry> returned = testable.retrieveByBacklog(project);
        assertEquals(2, returned.size());
    }
    
    @Test
    public void retrieveByBacklog_noEntries() {
        executeClassSql();
        Product product = new Product();
        product.setId(1);
        List<BacklogHourEntry> returned = testable.retrieveByBacklog(product);
        assertEquals(0, returned.size());
    }
}
