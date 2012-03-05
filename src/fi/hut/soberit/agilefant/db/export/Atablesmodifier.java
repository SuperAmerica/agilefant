package fi.hut.soberit.agilefant.db.export;

import java.sql.*;
import java.util.ArrayList;

public class Atablesmodifier {
    
    public class anonymColumn{
        public String tablename;
        public String columnname;
        public String datatype;
        public boolean isUnique;
        public anonymColumn()
        {
            this.isUnique = false;
        };
        public anonymColumn(String tablename, String columnname, String datatype, boolean unique)
        {
            this.tablename      = tablename;
            this.columnname     = columnname;
            this.datatype       = datatype;
            this.isUnique       = unique;
        }
    }
    
    private ArrayList<String>  tables;
    private ArrayList<anonymColumn> columns;
    private Connection connection = null; 
    private Statement statement = null;
    public Atablesmodifier() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // important - initalizeAnonymizedColumns must be called BEFORE initializeTables
        initializeAnonymizedColumns();
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
                // This way will copy column constraints (auto_increment, primarykey etc)
                statement.executeUpdate("CREATE TABLE " + "anonym_"+tables.get(counter)+" LIKE " +tables.get(counter));
                statement.executeUpdate("INSERT INTO "  + "anonym_"+tables.get(counter)+" SELECT * FROM " +tables.get(counter));
                counter++;
            }

            statement.close();
            connection.close();
        } 
        catch (Exception ex) {
            System.out.println(ex.getCause());
            System.out.println(ex.getMessage()); 
            deletetables();
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
    
    // Modify anonym_table columns value to be anonymous
    // Change all Columns that have string value to "length:[value length]" example "length:10"
    // If columns is UNIQUE then replace the value with its id
    public void anonymizeTables() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        try{
            DbPropertiesReader properties = new DbPropertiesReader();
            String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(sqlConnection, properties.getDbUsername(), properties.getDbPassword());
            statement = connection.createStatement();
            
            // Update columns values to be anonymous
            for (int i=0; i< this.columns.size(); i++)
            {
                String tableName  = this.columns.get(i).tablename;
                String columnName = this.columns.get(i).columnname;
                
                // unique column is replaced with id (PrimaryKey)
                if(this.columns.get(i).isUnique)
                {
                    String query = "UPDATE anonym_" + tableName + " SET " + columnName + " = id;";
                    statement.executeUpdate(query);                  
                }
                else // replace with length of the string
                {
                    String query = "UPDATE anonym_" + tableName + " SET " + columnName + " = CONCAT(\"length:\", LENGTH(" + columnName +"));";
                    statement.executeUpdate(query);
                }
            }
       
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("can not anonymize tables"+ e.getCause());
            System.out.println("can not anonymize tables"+ e.getMessage());
        }
    }
    
    //Get All tables from database
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
    
    public void initializeAnonymizedColumns() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        try {
                
            ArrayList dbcolumns = new ArrayList<anonymColumn>();
            
            DbPropertiesReader properties = new DbPropertiesReader();
            String sqlConnection = "jdbc:mysql://"+properties.getDbHost()+":3306"+ "/" +properties.getDbName();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(sqlConnection, properties.getDbUsername(), properties.getDbPassword());
           
            PreparedStatement ps = null;
            String query = "SELECT table_name, column_name, data_type, column_key " +
                           "FROM information_schema.columns " +
                           "WHERE table_schema = \"" + properties.getDbName() + "\"" +
                           "AND (data_type = \"longtext\" OR data_type = \"varchar\");";
            
            ps = connection.prepareStatement(query);
            ResultSet s = ps.executeQuery();
            while(s.next())
            {
                String tableName  = s.getString("table_name");
                String columnName = s.getString("column_name");
                String dataType   = s.getString("data_type");
                String columnKey  = s.getString("column_key");
                boolean isUnique = (columnKey.compareToIgnoreCase("UNI") == 0)?true:false;
                
                // Exclude all columns that are type of type string, but their values can't be changed  
                if((tableName.equalsIgnoreCase("hourentry")     && columnName.equalsIgnoreCase("DTYPE")) ||
                   (tableName.equalsIgnoreCase("backlogs")      && columnName.equalsIgnoreCase("backlogtype"))||
                   (tableName.equalsIgnoreCase("backlogs_aud")  && columnName.equalsIgnoreCase("backlogtype"))||
                   (tableName.equalsIgnoreCase("widgets")       && columnName.equalsIgnoreCase("type")))
                {
                    continue;
                }
                
                anonymColumn col = new anonymColumn(tableName,columnName,dataType, isUnique);
                dbcolumns.add(col);
            }
        
            ps.close();
            connection.close();
            this.columns = dbcolumns;
            
        } catch (SQLException e) {
            System.out.println("can not get columns from agilefant "+ e.getCause());
            System.out.println("can not get columns from agilefant "+ e.getMessage());
        }
    }
    
    
    public ArrayList<String> getOriginalTables()
    {
        return this.tables;
    }
    public ArrayList<anonymColumn> getAnonymColumn()
    {
        return this.columns;
    }
}


