package fi.hut.soberit.agilefant.db;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class AssignmentDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private AssignmentDAO assignmentDAO;
    
    private User user;
    
    private Interval interval;
    
    @Before
    public void setUp() {
        user = new User();
        user.setId(2);
    }
    
    @After
    public void tearDown() {
        this.user = null;
        this.interval = null;
    }
    
    @Test
    public void testAssigmentsInBacklogTimeframe_noInFrame() {
        executeClassSql();
        interval = new Interval(new DateTime(2009,1,1,0,0,0,0), new DateTime(2009,2,1,0,0,0,0));
        List<Assignment> actual = assignmentDAO.assigmentsInBacklogTimeframe(interval, user);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testAssigmentsInBacklogTimeframe_begins() {
        executeClassSql();
        interval = new Interval(new DateTime(2009,1,1,0,0,0,0), new DateTime(2009,6,5,0,0,0,0));
        List<Assignment> actual = assignmentDAO.assigmentsInBacklogTimeframe(interval, user);
        assertEquals(2, actual.size());
    }
    
    @Test
    public void testAssigmentsInBacklogTimeframe_ends() {
        executeClassSql();
        interval = new Interval(new DateTime(2009,6,6,0,0,0,0), new DateTime(2009,7,1,0,0,0,0));
        List<Assignment> actual = assignmentDAO.assigmentsInBacklogTimeframe(interval, user);
        assertEquals(2, actual.size());
    }
    
    @Test
    public void testAssigmentsInBacklogTimeframe() {
        executeClassSql();
        interval = new Interval(new DateTime(2009,6,2,0,0,0,0), new DateTime(2009,6,8,0,0,0,0));
        List<Assignment> actual = assignmentDAO.assigmentsInBacklogTimeframe(interval, user);
        assertEquals(2, actual.size());
    }
}
