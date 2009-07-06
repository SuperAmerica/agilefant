package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import fi.hut.soberit.agilefant.test.SampleDAO;
import fi.hut.soberit.agilefant.test.SampleEntity;

@ContextConfiguration
@Transactional
public class GenericDAOTest extends AbstractHibernateTests {

    @Autowired
    private SampleDAO sampleDAO;
    
    @Test
    public void testCreate() {
        SampleEntity entity = new SampleEntity();
        sampleDAO.create(entity);
        assertEquals(1, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, "samples"));
    }

    @Test
    public void testGet() {
        executeClassSql();
        SampleEntity entity = sampleDAO.get(1);
        assertEquals(1, entity.getId());
        assertEquals("Sample 1", entity.getName());
    }

    @Test
    public void testGetAll() {
        executeClassSql();
        Collection<SampleEntity> entities = sampleDAO.getAll();
        assertEquals(4, entities.size());
        Set<Integer> foundIds = new HashSet<Integer>();
        for (SampleEntity entity : entities) {
            foundIds.add(entity.getId());
        }
        assertTrue(foundIds.contains(1));
        assertTrue(foundIds.contains(2));
        assertTrue(foundIds.contains(3));
        assertTrue(foundIds.contains(4));
    }
    
    @Test
    public void testRemoveById() {
        executeClassSql();
        sampleDAO.remove(1);
        forceFlush();
        assertEquals(3, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, "samples"));
    }
    
    @Test
    public void testRemoveByObject() {
        executeClassSql();
        SampleEntity entity = sampleDAO.get(1);
        sampleDAO.remove(entity);
        forceFlush();
        assertEquals(3, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, "samples"));
    }
    
    @Test
    public void testStore() {
        executeClassSql();
        SampleEntity entity = sampleDAO.get(1);
        entity.setName("Changed");
        sampleDAO.store(entity);
        forceFlush();
        String name = simpleJdbcTemplate.queryForObject("SELECT name FROM samples WHERE id = ?", String.class, 1);
        assertEquals("Changed", name);
    }

    @Test
    public void testCount() {
        executeClassSql();
        assertEquals(4, sampleDAO.count());
    }
    
    @Test
    public void testExists_yes() {
        executeClassSql();
        assertTrue(sampleDAO.exists(1));
    }

    @Test
    public void testExists_no() {
        executeClassSql();        
        assertFalse(sampleDAO.exists(999));
    }

}
