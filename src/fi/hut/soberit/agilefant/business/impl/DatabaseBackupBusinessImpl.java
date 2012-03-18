package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.DatabaseBackupBusiness;
import fi.hut.soberit.agilefant.db.DatabaseBackupDAO;
import fi.hut.soberit.agilefant.model.DatabaseBackup;

@Service("databaseBackupBusiness")
@Transactional
public class DatabaseBackupBusinessImpl extends GenericBusinessImpl<DatabaseBackup> implements
        DatabaseBackupBusiness {

    private DatabaseBackupDAO databaseBackupDAO;
    
    @Autowired
    public void setDatabaseBackupDAO(DatabaseBackupDAO dbbDAO) {
        this.databaseBackupDAO = dbbDAO;
        this.genericDAO = dbbDAO;
    }

    public DatabaseBackupBusinessImpl() {
        super(DatabaseBackup.class);
    }
}
