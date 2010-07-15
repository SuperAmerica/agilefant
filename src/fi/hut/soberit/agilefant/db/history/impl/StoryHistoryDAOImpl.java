package fi.hut.soberit.agilefant.db.history.impl;

import javax.persistence.NoResultException;

import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.model.Story;

@Repository("storyHistoryDAO")
public class StoryHistoryDAOImpl extends GenericHistoryDAOImpl<Story> implements
        StoryHistoryDAO {

    public StoryHistoryDAOImpl() {
        super(Story.class);
    }

    public Story retrieveClosestRevision(int storyId, int revisionId) {
        AuditQuery query = this.getAuditReader().createQuery()
                .forRevisionsOfEntity(Story.class, true, true);
        query.add(AuditEntity.revisionNumber().ge(revisionId));
        query.setMaxResults(1);
        try {
            return (Story) query.getSingleResult();
        } catch (NoResultException nre) {

        }
        try {
            query = this.getAuditReader().createQuery().forRevisionsOfEntity(
                    Story.class, true, true);
            query.add(AuditEntity.revisionNumber().lt(revisionId));
            query.addOrder(AuditEntity.revisionProperty("id").desc());
            query.setMaxResults(1);
            return (Story) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
