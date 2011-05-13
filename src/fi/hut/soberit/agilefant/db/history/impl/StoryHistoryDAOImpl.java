package fi.hut.soberit.agilefant.db.history.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

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
            query = this.getAuditReader().createQuery()
                    .forRevisionsOfEntity(Story.class, true, true);
            query.add(AuditEntity.revisionNumber().lt(revisionId));
            query.addOrder(AuditEntity.revisionProperty("id").desc());
            query.setMaxResults(1);
            return (Story) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Long> calculateAccessCounts(DateTime start, DateTime end,
            User user) {
        
        AuditQuery query = this.getAuditReader().createQuery()
                .forRevisionsOfEntity(Story.class, true, false);
        query.add(AuditEntity.revisionProperty("userId").eq(user.getId()));
        query.add(AuditEntity.revisionProperty("timestamp").between(start.getMillis(), end.getMillis()));
        List<Story> data = query.getResultList();
        Map<Integer, Long> result = new HashMap<Integer, Long>();
        for(Story row : data) {
            if(!result.containsKey(row.getId())) {
                result.put(row.getId(), 0l);
            }
            result.put(row.getId(), result.get(row.getId()) + 1);
        }
        return result;
    }

}
