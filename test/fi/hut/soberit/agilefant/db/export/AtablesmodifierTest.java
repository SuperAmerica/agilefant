package fi.hut.soberit.agilefant.db.export;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class AtablesmodifierTest extends AbstractHibernateTests{

    @Autowired
    private Atablesmodifier atablesmodifier;
    private Connection connection = null; 
    private Statement statement = null;
    private String usr = "sa";
    private String pwd = "sa";
    private String url = "jdbc:h2:mem:aeftest";
    private String driver = "org.h2.Driver";
    @Test
    public void testInitializeTables() {
        atablesmodifier.dbinfo.setDbName("aeftest");
        atablesmodifier.dbinfo.setUrl(url);
        atablesmodifier.dbinfo.setHostname("h2");
        atablesmodifier.dbinfo.setUsername(usr);
        atablesmodifier.dbinfo.setPassword(pwd);
        
        executeClassSql();
//        try {
//            atablesmodifier = new Atablesmodifier();            
//        }
//        catch(Exception ex) {
//            fail("Exception thrown");
//        }
        assertFalse(atablesmodifier.getOriginalTables().isEmpty());       
    }

    @SuppressWarnings("unused")
    @Test
    public void testDuplicaTables() {
        executeClassSql();
        try {
            atablesmodifier = new Atablesmodifier();
            atablesmodifier.dublicaTables();
        }
        catch(Exception ex) {
            fail("Exception thrown");
        }
        String sqlConnection = url;
        int tablesize= atablesmodifier.getOriginalTables().size();

        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection
                    (sqlConnection, usr, pwd);
            statement = connection.createStatement();

            ResultSet results = statement.executeQuery("SELECT * FROM anonym_settings");
            assertTrue(results.first());

            atablesmodifier.deletetables();
            statement.close();
            connection.close();
        } 
        catch (Exception ex) {
            fail("Exception thrown");

        }
    }

    @Test
    public void testDeletetables() {
        executeClassSql();
        try {
            atablesmodifier = new Atablesmodifier(); 
            atablesmodifier.dublicaTables();
            atablesmodifier.deletetables();
        }
        catch(Exception ex) {
            fail("Exception thrown");
        }
        String sqlConnection = url; 
        int tablesize= atablesmodifier.getOriginalTables().size();

        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection
                    (sqlConnection, usr, pwd);
            statement = connection.createStatement();

            ResultSet results = statement.executeQuery("SELECT * FROM anonym_settings");
            assertFalse(results.first());

            statement.close();
            connection.close();
        } 
        catch (MySQLSyntaxErrorException exm)
        {
            assertTrue(true);
        }
        catch (Exception ex) {
            fail("Exception thrown");

        }
    }
    
    

}
