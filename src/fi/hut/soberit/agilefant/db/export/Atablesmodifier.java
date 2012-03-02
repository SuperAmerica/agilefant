package fi.hut.soberit.agilefant.db.export;

import java.sql.*;
import java.util.ArrayList;






public class Atablesmodifier {

    private ArrayList<String>  tables;
    private Connection connection = null; 
    private Statement statement = null;
    public Atablesmodifier() {
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
    
    // Todo - get tables name from mysql
    public void initializeTables(){
        ArrayList dbtables = new ArrayList<String>();
        dbtables.add("Holiday");
        dbtables.add("HolidayAnomaly");
        dbtables.add("agilefant_revisions");
        dbtables.add("assignment");
        dbtables.add("assignment_AUD");
        dbtables.add("backlogs");
        dbtables.add("backlogs_AUD");        
        dbtables.add("history_backlogs");
        dbtables.add("history_iterations");
        dbtables.add("hourentries");
        dbtables.add("labels");
        dbtables.add("settings");
        dbtables.add("stories");
        dbtables.add("stories_AUD");
        dbtables.add("story_access");
        dbtables.add("story_user");
        dbtables.add("story_user_AUD");
        dbtables.add("storyrank");
        dbtables.add("storyrank_AUD");
        dbtables.add("task_user");
        dbtables.add("task_user_AUD");
        dbtables.add("tasks");
        dbtables.add("tasks_AUD");
        dbtables.add("team_user");
        dbtables.add("teams");
        dbtables.add("users");
        dbtables.add("users_AUD");
        dbtables.add("whatsnextentry");
        dbtables.add("widgetcollections");
        dbtables.add("widgets");
        
        this.tables = dbtables;
    }
}


