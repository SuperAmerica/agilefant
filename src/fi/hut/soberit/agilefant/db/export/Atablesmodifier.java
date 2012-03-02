package fi.hut.soberit.agilefant.db.export;

import java.sql.*;
import java.util.ArrayList;






public class Atablesmodifier {

    private ArrayList<String>  tables;
    private Connection connection = null; 
    private Statement statement = null;
    public Atablesmodifier(ArrayList<String>  tables) {
        this.tables=tables;
    }


    public void dublicaTables(){
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
            int result=0;
            String quary="";
            // start transaction & commit commented out because mysql's create table automatically commits which mean these wont actually do anything. 

            //  result = statement.executeUpdate("START TRANSACTION");
            while (tablesize> counter){
                result = statement.executeUpdate("CREATE TABLE " + "anonym_"+tables.get(counter)+" SELECT * FROM " +tables.get(counter));
                //previous command in string "CREATE TABLE anonym_team  SELECT * FROM  team || which copies tables one by one in while loop"
                counter++;
            }
            // result = statement.executeUpdate("COMMIT");
            System.out.println("debug: tables copied");

            statement.close();
            connection.close();
        } 
        catch (Exception ex) {
            System.out.println(ex.getCause());
            System.out.println(ex.getMessage()); //what to do if errors
            deletetables(); //deletes tables if tables exists we might leave it, but helps debugging a lot since you have to run code twice to get db to original state.
        }
    }

    public void deletetables() {
        /**
         * Deletes anonymized tables one by one           
         * 
         */
        try {
            int counter=0;
            int tablesize = tables.size();  
            int result=0;
            while (tablesize>counter) {
                result= statement.executeUpdate("Drop table anonym_" +tables.get(counter));
                counter++;
            }
            System.out.println("debug: tables deleted");
        } catch (SQLException e) {
            System.out.println("delete "+ e.getCause());
            System.out.println("delete "+ e.getMessage());
        }

    }
}


