package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.State;

/**
 * Hibernate implementation of BacklogDAO interface using GenericDAOHibernate.
 */
public class BacklogDAOHibernate extends GenericDAOHibernate<Backlog> implements
        BacklogDAO {

    public BacklogDAOHibernate() {
        super(Backlog.class);
    }

    /** {@inheritDoc} */
    public int getNumberOfDoneBacklogItems(int backlogId) {
        return Integer.valueOf(super.getHibernateTemplate().find("select count(*) from BacklogItem b "
                + "where b.backlog = ? and b.state = ?",
                new Object[] { this.get(backlogId), State.DONE }).get(0).toString());
    }
    
    public int getNumberOfDoneBacklogItems(Backlog backlog) {
        return this.getNumberOfDoneBacklogItems(backlog.getId());
    }
}
