package fi.hut.soberit.agilefant.util;

public class DbConnectionInfo {
    private static String password;
    private static String username;
    private static String url;
    private static String dbName;
    private static String hostname;
    
    public DbConnectionInfo()
    { }
    
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
        if (DbConnectionInfo.dbName == null)
        {
            // TODO @braden find better way to get the database name/host
            String[] urlParts = url.split("/");
            setHostname(urlParts[2].substring(0, urlParts[2].indexOf(":")));
            String dbName = urlParts[3].split("[^(A-Za-z)]")[0];
            setDbName(dbName);
        }
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
