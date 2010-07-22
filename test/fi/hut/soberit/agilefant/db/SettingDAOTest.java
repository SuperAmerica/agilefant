package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.model.Setting;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class SettingDAOTest extends AbstractHibernateTests {
    
    @Autowired    
    private SettingDAO testable;
    
    @Test
    public void getByName() {
        executeClassSql();
        Setting actual = testable.getByName("sampleSetting");
        assertEquals("test", actual.getValue());
    }
    
    @Test
    public void getByName_noSuchSetting() {
        executeClassSql();
        assertNull(testable.getByName("noSuchSetting"));
    }
    
}
