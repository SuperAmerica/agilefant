package fi.hut.soberit.agilefant.db.history.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;

import fi.hut.soberit.agilefant.db.history.GenericHistoryDAO;
import fi.hut.soberit.agilefant.model.AgilefantRevisionEntity;
import fi.hut.soberit.agilefant.transfer.HistoryRowTO;

public abstract class GenericHistoryDAOImpl<T> implements GenericHistoryDAO<T> {
    private Class<?> clazz;
    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public GenericHistoryDAOImpl(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    protected AuditReader getAuditReader() {
        return AuditReaderFactory.get(this.sessionFactory
                .getCurrentSession());
    }

    protected AuditQuery createQuery() {
        return this.getAuditReader().createQuery().forRevisionsOfEntity(this.clazz,
                false, true);
    }

    @SuppressWarnings("unchecked")
    public List<HistoryRowTO> retrieveLatestChanges(int objectId, Integer numberOfChanges) {
        AuditQuery query = this.createQuery();
        query.add(AuditEntity.id().eq(objectId));
        query.addOrder(AuditEntity.revisionNumber().desc());
        if (numberOfChanges != null) {
            query.setMaxResults(numberOfChanges);
        }
        List<Object[]> rows = query.getResultList();
        List<HistoryRowTO> entries = new ArrayList<HistoryRowTO>();
        for(Object[] row : rows) {
            entries.add(new HistoryRowTO((AgilefantRevisionEntity)row[0], row[1]));
        }
        return entries;
    }
}
