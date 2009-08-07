package fi.hut.soberit.agilefant.db.history.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GenericHistory<T> {
    private AuditReader auditReader = null;
    private Class<?> clazz;
    private SessionFactory sessionFactory;

    @PostConstruct
    public void init() {
        if (auditReader == null) {
            throw new IllegalStateException("AuditReader cannot be null");
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.auditReader = AuditReaderFactory.get(this.sessionFactory
                .getCurrentSession());
    }

    protected AuditReader getAuditReader() {
        return this.auditReader;
    }

    protected AuditQuery createQuery() {
        return this.auditReader.createQuery().forRevisionsOfEntity(this.clazz,
                false, true);
    }

    public List<?> retrieveLatestChanges(int objectId, Integer numberOfChanges) {
        AuditQuery query = this.createQuery();
        query.add(AuditEntity.id().eq(objectId));
        query.addOrder(AuditEntity.property("revisionDate").desc());
        if (numberOfChanges != null) {
            query.setMaxResults(numberOfChanges);
        }
        return query.getResultList();
    }
}
