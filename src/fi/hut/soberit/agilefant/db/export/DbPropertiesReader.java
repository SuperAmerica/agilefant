package fi.hut.soberit.agilefant.db.export;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads database properties from the property file for code access
 */
public class DbPropertiesReader {

    private Properties properties;
    
    /**
     * 
     */
    public DbPropertiesReader() {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("database-mysql5.properties");

        this.properties = new Properties();

        try {
            this.properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException nullPtrE) { // for TestStreams
            this.properties.setProperty("hibernate.connection.username",
                    "agilefant");
            this.properties.setProperty("hibernate.connection.password",
                    "agilefant");
            this.properties.setProperty("hibernate.connection.url",
                    "jdbc:mysql://localhost/agilefant?relaxAutoCommit\\=true&amp;autoReconnect\\=true&amp;useUnicode\\=true&amp;characterEncoding\\=utf-8&amp;autoReconnectForPools\\=true");
        }
    }

    /**
     * Returns the database username.
     */
    public String getDbUsername() {
        return this.properties.getProperty("hibernate.connection.username");
    }

    /**
     * Returns the database password.
     */
    public String getDbPassword() {
        return this.properties.getProperty("hibernate.connection.password");
    }

    /**
     * Returns the database name from the connection url. example: "agilefant"
     */
    public String getDbName() {
        // TODO: use regex for more flexibility? 
        
        // e.g. "jdbc:mysql://localhost/agilefant?..."
        String url = this.properties.getProperty("hibernate.connection.url");
        String startPattern = "jdbc:mysql://";

        int begin = url.indexOf('/', startPattern.length()) + 1;
        int end = url.indexOf('?', begin);

        return url.substring(begin, end);
    }

    /**
     * Returns the database location from the connection url. example:
     * "localhost"
     */
    public String getDbHost() {
        // TODO: use regex for more flexibility?
                
        // e.g. "jdbc:mysql://localhost/agilefant?..."
        String url = this.properties.getProperty("hibernate.connection.url");
        String startPattern = "jdbc:mysql://";

        int begin = startPattern.length();
        int end = url.indexOf('/', begin);

        return url.substring(begin, end);
    }
}