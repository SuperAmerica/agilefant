package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import fi.hut.soberit.agilefant.db.HistoryDAO;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.History;
import fi.hut.soberit.agilefant.model.HistoryEntry;

public class HistoryDAOHibernate extends
        GenericDAOHibernate<History<?>> implements HistoryDAO {

    public HistoryDAOHibernate() {
        super(History.class);
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public HistoryEntry<BacklogHistory> getEntryByDate(int backlogId, java.util.Date date) {
        
        String hql = "FROM HistoryEntry he, Backlog blog WHERE he.history = blog.backlogHistory " +
        		"AND he.date <= ? AND blog.id = ? ORDER BY date DESC LIMIT 1";
        Object[] params = new Object[] { new java.sql.Date(date.getTime()), backlogId };
        
        List list = super.getHibernateTemplate().find(hql, params);
                
        try {
            Object[] ob = (Object[])list.get(0);
            return (HistoryEntry<BacklogHistory>)ob[0];
        } catch (Exception e) {
            return null;
        }
    }
}
