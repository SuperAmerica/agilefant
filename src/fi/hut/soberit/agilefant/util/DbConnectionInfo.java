package fi.hut.soberit.agilefant.util;

import java.net.URI;

public class DbConnectionInfo {
    private static String password;
    private static String username;
    private static String url;
    private static String dbName;
    private static String hostname;     
    private static String driver;
    
    public DbConnectionInfo()
    { }
    
    public String getDriver() {
        return driver;
    }
    
    public void setDriver(String driver) {
        DbConnectionInfo.driver = driver;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        DbConnectionInfo.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        DbConnectionInfo.username = username;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        String cleanURI = url.substring(5);
        URI uri = URI.create(cleanURI);
        this.setHostname(uri.getHost());
        this.setDbName(uri.getPath().substring(1));
        DbConnectionInfo.url = url;
    }
    public void setHostname(String hostname)
    {
        DbConnectionInfo.hostname = hostname;
    }
    public String getHostname()
    {
        return DbConnectionInfo.hostname;
    }
    public void setDbName(String dbName)
    {
        DbConnectionInfo.dbName = dbName;
    }
    public String getDbName()
    {
        return DbConnectionInfo.dbName;
    }
}
