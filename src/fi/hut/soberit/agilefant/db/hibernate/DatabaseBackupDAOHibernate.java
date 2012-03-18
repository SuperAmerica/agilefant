package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.DatabaseBackupDAO;
import fi.hut.soberit.agilefant.model.DatabaseBackup;

@Repository("databaseBackupDAO")
public class DatabaseBackupDAOHibernate extends GenericDAOHibernate<DatabaseBackup> implements
        DatabaseBackupDAO {
    public DatabaseBackupDAOHibernate() {
        super(DatabaseBackup.class);
    }
}
