package fi.hut.soberit.agilefant.db.export;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generates a zipped database dump with mysqldump and provides it in a 
 * ByteArrayOutputStream for access.
 */
public class DbBackupStreamGenerator {

    private String dbLogin;
    private String dbPassword;
    private String dbHost;
    private String dbName;
    private boolean anonymouse=false;
    private String errorMessage;
    private ByteArrayOutputStream zippedDbOutputStream;

    /**
     * Initializes the class for taking a mysql dump 
     * @param dbName Database name, e.g. "agilefant"
     * @param dbHost Database location, e.g. "localhost" or "127.0.0.1" 
     * @param dbUsername Database username, e.g. "agilefant"
     * @param dbPassword Database password, e.g. "secret"
     */
    public DbBackupStreamGenerator(String dbName, String dbHost, String dbUsername, String dbPassword) {

        this.dbLogin = dbUsername;
        this.dbName = dbName;
        this.dbPassword = dbPassword;
        this.dbHost = dbHost;

        this.errorMessage = "";
        this.zippedDbOutputStream = new ByteArrayOutputStream();
    }


    /**
     * Returns dumped database as zipped ByteArrayOutputStream. It's 
     * usually a good idea to call generate first 
     */
    public ByteArrayOutputStream getZippedDbOutputStream() {        
        return this.zippedDbOutputStream;
    }


    /**
     * Calls mysqldump and generates zipped stream returns 0 if backup was
     * saved correctly, -1 if try fails, and >1 are sql errors
     * 
     */
    public int generateZippedDbOutputStream() { 
        String dumpcommand = ("mysqldump" + " -h " + dbHost + " -u " + dbLogin + " -p" + dbPassword + " " + dbName);
        return executeDumpCmd(dumpcommand, "fantbackup.sql");
    }

    /**
     * Calls mysqldump and generates zipped stream returns 0 if backup was
     * saved correctly, -1 if try fails, and >1 are sql errors
     * Only generate anonymous data
     */
    public int generateZippedAnonymousDbOutputStream(ArrayList<String> excludedTables) { 

        String dumpcommand = ("mysqldump" + " -h " + dbHost + " -u " + dbLogin + " -p" + dbPassword + " " + dbName);

        // ignore original tables
        for(int i=0; i< excludedTables.size(); i++ )
        {
            dumpcommand += " --ignore-table=" + dbName + "." + excludedTables.get(i);
        }
        anonymouse=true;
        return executeDumpCmd(dumpcommand, "AnonymousFantBackup.sql");
    }

    public int executeDumpCmd(String dumpcommand, String fileName)
    {

        try {
            Runtime dbb = Runtime.getRuntime();
            Process process = dbb.exec(dumpcommand);
            InputStream in = process.getInputStream();
            zippedDbOutputStream = new ByteArrayOutputStream();
            ZipOutputStream outzip = new ZipOutputStream(zippedDbOutputStream);
            int len;
            if (anonymouse==true) {
                Sqlfilecontentgenerator sqlscript = new Sqlfilecontentgenerator();
                outzip.putNextEntry(new ZipEntry("importscript.sql"));
                               InputStream inscript = sqlscript.getScriptByteStream();
                 
                while ((len = inscript.read()) != -1) {
                    outzip.write(len);                
                }
            outzip.closeEntry();
            }
            
            outzip.putNextEntry(new ZipEntry(fileName));

            // loop through the inputstream that contains mysqldump output and zip it
               
            while ((len = in.read()) != -1) {
                outzip.write(len);                
            }

            in.close();
            outzip.closeEntry();
            outzip.close();            

            // read the error stream to a field
            this.readProcessErrors(process);            

            // 0 means success, 1+ are mysqldump error codes
            int exitval = process.exitValue();            
            return exitval;

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return -1; // return -1 if try didn't finish
    }

    /**
     * Stores error messages from process's error stream for later access
     * @param stdErr
     * @throws IOException
     */
    private void readProcessErrors(Process process) throws IOException {

        InputStream stdErr = process.getErrorStream();

        InputStreamReader stdErrReader = new InputStreamReader(stdErr);
        BufferedReader bufferedStdErrReader = new BufferedReader(stdErrReader);

        String line = null;
        while ((line = bufferedStdErrReader.readLine()) != null) {
            this.errorMessage = this.errorMessage + line;
        }
    }    


    /**
     * Shows error message generated by mysqldump Returns errormessage
     */    
    public String getErrorMessages() {
        return errorMessage;
    }
}
