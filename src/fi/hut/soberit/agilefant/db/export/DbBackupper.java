package fi.hut.soberit.agilefant.db.export;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import fi.hut.soberit.agilefant.util.DbConnectionInfo;
/**
 * Provides a zipped database dump in an output stream based on
 * Agilefant database properties
 * 
 */
public class DbBackupper {

    private ByteArrayOutputStream dbOutputStream;
    private DbConnectionInfo dbinfo;
    /**
     * 
     */
    public DbBackupper() {
        dbinfo = new DbConnectionInfo();
        dbOutputStream = new ByteArrayOutputStream();
    }

    /**Calls DbConfReader and Dbbackup to generate zipped stream
     * Returns ByteArrayOutputStream that is zipped DBdump from mysqldump or null if generating
     * stream failed for some reason.
     */
    public ByteArrayOutputStream generateDBDumpStream() {
        
        DbBackupStreamGenerator dbbackup = new DbBackupStreamGenerator(dbinfo.getDbName(),
                                         dbinfo.getHostname(),
                                         dbinfo.getUsername(),
                                         dbinfo.getPassword());                                         
        
        
        int exitvalue = dbbackup.generateZippedDbOutputStream();     
        
        if (exitvalue != 0) {
            //exit value -5 is result of failure in try catch. which shouldn't happen. values +0
            // are result for mysqldump failure debugging getErrorMessages() will give more info
            return null;    
        }
        
        this.dbOutputStream = dbbackup.getZippedDbOutputStream();
        return dbbackup.getZippedDbOutputStream();
    }
    
    /**Anonymous dumping
     * Calls DbConfReader and Dbbackup to generate zipped stream
     * Returns ByteArrayOutputStream that is zipped DBdump from mysqldump or null if generating
     * stream failed for some reason.
     */
    public ByteArrayOutputStream generateAnonymousDBDumpStream(ArrayList<String> excludedTables) {
        
        DbBackupStreamGenerator dbbackup = new DbBackupStreamGenerator(dbinfo.getDbName(),
                                         dbinfo.getHostname(),
                                         dbinfo.getUsername(),
                                         dbinfo.getPassword());                                         
        
        
        int exitvalue = dbbackup.generateZippedAnonymousDbOutputStream(excludedTables);     
        
        if (exitvalue != 0) {
            //exit value -5 is result of failure in try catch. which shouldn't happen. values +0
            // are result for mysqldump failure debugging getErrorMessages() will give more info
            return null;    
        }
        
        this.dbOutputStream = dbbackup.getZippedDbOutputStream();
        return dbbackup.getZippedDbOutputStream();
    }
    
    /**
     * Returns and output stream that contains the zipped dbdump
     */
    public ByteArrayOutputStream getDbOutputStream() {
        return this.dbOutputStream;
    }
    
}
