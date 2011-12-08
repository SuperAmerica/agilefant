package fi.hut.soberit.agilefant.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.db.export.DbBackupper;


@Component("dbExportAction")
@Scope("prototype")
public class DatabaseExportAction extends ActionSupport {
    
    private static final long serialVersionUID = -1639488740106383276L;
    
    private DbBackupper takeDbBackup;
    private ByteArrayOutputStream databaseStream;
    
    
    public String edit()  {
        this.takeDbBackup = new DbBackupper();
       
        return Action.SUCCESS;
    }
    
    public String generateDatabaseExport() {
        this.takeDbBackup = new DbBackupper();
        this.databaseStream = takeDbBackup.generateDBDumpStream();
        
        return Action.SUCCESS;
    }
    
    public InputStream getDatabaseStream() {        
        return new ByteArrayInputStream(this.databaseStream.toByteArray());               
    }
    
    public void setDatabaseStream(ByteArrayOutputStream databaseStream) { 
        this.databaseStream = databaseStream; 
    }
    
}
