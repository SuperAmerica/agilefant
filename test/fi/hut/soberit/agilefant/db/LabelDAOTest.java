package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.model.Label;
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
    
    @Test
    public void testSearchLabel(){
        executeClassSql();
        List<Label> list = this.labelDAO.lookupLabelsLike("Kis");
        assertEquals(1, list.size());
        assertEquals("kissa", list.get(0).getName());
        assertEquals(1, list.get(0).getId());
    }
    
    @Test
    public void testSearchLabel_NotFound() {
        executeClassSql();
        List<Label> list = this.labelDAO.lookupLabelsLike("Watti");
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void testSearchLabel_MultipleFound() {
        executeClassSql();
        List<Label> list = this.labelDAO.lookupLabelsLike("Ma");
        assertEquals(2, list.size());
        for(Label label : list ) {
            assertTrue(label.getName().equals("matti") || label.getName().equals("mauno")); 
        }
    }
}
