package fi.hut.soberit.agilefant.db.export;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class AtablesmodifierTest {

    @Autowired
    private Atablesmodifier atablesmodifier;
    private Connection connection = null; 
    private Statement statement = null;
    @Test
    public void testInitializeTables() {
        
        try {
            atablesmodifier = new Atablesmodifier();            
        }
        catch(Exception ex) {
            fail("Exception thrown");
        }
        assertFalse(atablesmodifier.getOriginalTables().isEmpty());       
    }

    @SuppressWarnings("unused")
    @Test
    public void testDuplicaTables() {
        DbPropertiesReader properties = new DbPropertiesReader();
        try {
            atablesmodifier = new Atablesmodifier();
            atablesmodifier.dublicaTables();
        }
        catch(Exception ex) {
            fail("Exception thrown");
        }
        String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();     //may need port in the future
        int tablesize= atablesmodifier.getOriginalTables().size();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection
                    (sqlConnection, properties.getDbUsername(), properties.getDbPassword());
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
        DbPropertiesReader properties = new DbPropertiesReader();
        try {
            atablesmodifier = new Atablesmodifier(); 
            atablesmodifier.dublicaTables();
            atablesmodifier.deletetables();
        }
        catch(Exception ex) {
            fail("Exception thrown");
        }
        String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();     //may need port in the future
        int tablesize= atablesmodifier.getOriginalTables().size();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection
                    (sqlConnection, properties.getDbUsername(), properties.getDbPassword());
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
