package fi.hut.soberit.agilefant.db.export;

import java.sql.*;
import java.util.ArrayList;






public class Atablesmodifier {

    private ArrayList<String>  tables;
    private Connection connection = null; 
    private Statement statement = null;
    public Atablesmodifier() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        initializeTables();
    }


    public void dublicaTables() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        /**
         * generates anonymity tables with orginal non-anonymized data. 
         * 
         */

        DbPropertiesReader properties = new DbPropertiesReader();
        String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();     //may need port in the future
        int tablesize= tables.size();
        int counter=0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection
                    (sqlConnection, properties.getDbUsername(), properties.getDbPassword());
            statement = connection.createStatement();

            while (tablesize> counter){
                statement.executeUpdate("CREATE TABLE " + "anonym_"+tables.get(counter)+" SELECT * FROM " +tables.get(counter));
                counter++;
            }

            statement.close();
            connection.close();
        } 
        catch (Exception ex) {
            System.out.println(ex.getCause());
            System.out.println(ex.getMessage()); //what to do if errors
            deletetables(); //deletes tables if tables exists we might leave it, but helps debugging a lot since you have to run code twice to get db to original state.
        }
    }

    public void deletetables() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        /**
         * Deletes anonymized tables one by one           
         * 
         */
        DbPropertiesReader properties = new DbPropertiesReader();
        String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(sqlConnection, properties.getDbUsername(), properties.getDbPassword());
            statement = connection.createStatement();
            
            int counter=0;
            int tablesize = tables.size();  
            while (tablesize>counter) {
                statement.executeUpdate("Drop table anonym_" +tables.get(counter));
                counter++;
            }
            statement.close();
            connection.close();
            
        } catch (SQLException e) {
            System.out.println("delete "+ e.getCause());
            System.out.println("delete "+ e.getMessage());
        }
    }
    
    public void anonymizeTables()
    {
    // Todo: anonymize all columns of the new tables    
    }
    
    //Get All tables from agilefant
    public void initializeTables() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        try {
                
            ArrayList dbtables = new ArrayList<String>();
            
            DbPropertiesReader properties = new DbPropertiesReader();
            String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(sqlConnection, properties.getDbUsername(), properties.getDbPassword());
           
            PreparedStatement ps = null;
            String query = "select table_name from information_schema.tables WHERE table_schema = \"" + properties.getDbName() + "\";";
           
            ps = connection.prepareStatement(query);
            ResultSet s = ps.executeQuery();
            while(s.next())
            {
                dbtables.add(s.getString("table_name"));
            }
        
            ps.close();
            connection.close();
            this.tables = dbtables;
            
        } catch (SQLException e) {
            System.out.println("can not get tables from agilefant "+ e.getCause());
            System.out.println("can not get tables from agilefant "+ e.getMessage());
        }
    }
    
    public ArrayList<String> getOriginalTables()
    {
        return this.tables;
    }
}


