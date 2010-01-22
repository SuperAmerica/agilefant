package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class LabelDAOTest extends AbstractHibernateTests {

    @Autowired
    private LabelDAO labelDAO;
    
    @Test
    public void duplicateLabelExists(){
        executeClassSql();
        Story story = new Story();
        story.setId(1);
        assertEquals(true, this.labelDAO.labelExists("Kissa", story));
    }
    
    @Test
    public void noDuplicateLabelExists(){
        executeClassSql();
        Story story = new Story();
        story.setId(1);
        assertEquals(false, this.labelDAO.labelExists("Katti", story));
    }
}
