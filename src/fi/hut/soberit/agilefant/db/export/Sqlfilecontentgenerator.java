package fi.hut.soberit.agilefant.db.export;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.hut.soberit.agilefant.util.DbConnectionInfo;


public class Sqlfilecontentgenerator 
{
    // private static final String dbinfo = null;
    private ArrayList<String> listOfTables = new ArrayList<String>();
    private DbConnectionInfo dbinfo;
    private Connection connection = null; 
    private String sqlscript= "";
    public Sqlfilecontentgenerator() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException 
    {
        this.dbinfo = new DbConnectionInfo();
        getdbtables();
        generateScriptString();
    }



    private void getdbtables() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
    {

        PreparedStatement ps = null;
        String sqlConnection = this.dbinfo.getUrl(); 
        Class.forName(dbinfo.getDriver()).newInstance();
        connection = DriverManager.getConnection(sqlConnection, dbinfo.getUsername(), dbinfo.getPassword());
        String query = "select  table_name FROM information_schema.tables WHERE table_schema = 'agilefant' AND table_name LIKE 'anonym_%'";
        ps = connection.prepareStatement(query);
        ResultSet s = ps.executeQuery();
        while(s.next())
        {
            listOfTables.add(s.getString("table_name"));
        }

    }
    private void generateScriptString() 
    {

        int tablesize=listOfTables.size();
        int counter=0;
        while (tablesize>counter) 
        {
            sqlscript= sqlscript + "RENAME TABLE "+ listOfTables.get(counter) + " TO " + listOfTables.get(counter).substring(listOfTables.get(counter).indexOf('_')+1)+ ";" + System.getProperty("line.separator");    
            counter++;
        }
    }


    public InputStream getScriptByteStream()
    {
        InputStream sqlscriptstream = new ByteArrayInputStream(sqlscript.getBytes());
        return sqlscriptstream;
    }
}
